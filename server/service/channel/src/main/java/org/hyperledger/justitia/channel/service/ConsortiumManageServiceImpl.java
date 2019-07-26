package org.hyperledger.justitia.channel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.justitia.service.face.channel.ConsortiumManageService;
import org.hyperledger.justitia.service.face.channel.bean.ConsortiumInfo;
import org.hyperledger.justitia.service.face.identity.bean.OrdererInfo;
import org.hyperledger.justitia.service.face.identity.bean.OrganizationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConsortiumManageServiceImpl implements ConsortiumManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsortiumManageServiceImpl.class);

    @Autowired
    public ConsortiumManageServiceImpl(NodeReader nodeReader, OrganizationReader organizationReader, ChannelManager channelManager, ConfigTxLator configtxlator) {
        this.nodeReader = nodeReader;
        this.organizationReader = organizationReader;
        this.channelManager = channelManager;
        this.configtxlator = configtxlator;
    }

    @Override
    public List<ConsortiumInfo> getConsortiums(String ordererId) {
        OrdererInfo orderer = getOrderer(ordererId);
        byte[] systemChainConfigBytes = getSystemChainConfigBytes(orderer.getSystemChainId());
        Configtx.Config systemChainConfig;
        try {
            systemChainConfig = Configtx.Config.parseFrom(systemChainConfigBytes);
        } catch (InvalidProtocolBufferException e) {
            throw new ConsortiumManageException("通道配置数据解析失败");
        }

        Configtx.ConfigGroup consortiums = systemChainConfig.getChannelGroup().getGroupsMap().get("Consortiums");
        List<ConsortiumInfo> consortiumsInfo = new ArrayList<>();
        if (consortiums != null) {
            Map<String, Configtx.ConfigGroup> consortiumsGroupsMap = consortiums.getGroupsMap();
            for (Map.Entry<String, Configtx.ConfigGroup> groupEntry : consortiumsGroupsMap.entrySet()) {
                String consortiumName = groupEntry.getKey();
                Configtx.ConfigGroup consortium = groupEntry.getValue();
                Set<String> membersName = consortium.getGroupsMap().keySet();
                consortiumsInfo.add(new ConsortiumInfo(consortiumName, membersName));
            }
        }
        return consortiumsInfo;
    }

    @Override
    public void addConsortiumMember(String ordererName, String consortiumName, String orgName, String orgConfig) {
        //读取用户上传的组织配置文件
        Map orgConfigJson;
        try {
            orgConfigJson = new ObjectMapper().readValue(orgConfig, Map.class);
        } catch (Throwable  e) {
            throw new ConsortiumManageException("用户上传的组织配置文件读取失败,文件不是json数据");
        }
        //获取系统通道配置
        OrdererInfo orderer = getOrderer(ordererName);
        byte[] systemChainConfigBytes = getSystemChainConfigBytes(orderer.getSystemChainId());
        //解码通道配置
        Map configMap = decodeChainConfig(systemChainConfigBytes);
        //修改通道配置
        Map consortiums = getConsortiums(configMap);
        if (consortiums == null) {
            throw new ConsortiumManageException("系统通道配置读取失败，配置格式不正确");
        }
        if (consortiums.containsKey(consortiumName)) {
            Map consortium = (Map) consortiums.get(consortiumName);
            if (consortium == null) {
                throw new ConsortiumManageException("获取到的联盟" + consortiumName + "配置信息为空，无法添加组织到联盟");
            }
            if (consortium.containsKey("groups")) {
                Map groups = (Map) consortium.get("groups");
                if (groups == null) {
                    groups = new HashMap();
                } else if (groups.containsKey(orgName)) {
                    throw new ConsortiumManageException("联盟" + consortiumName + "中已存在名为" + orgName + "的成员");
                }
                groups.put(orgName, orgConfigJson);
            } else {
                Map groups = new HashMap<>();
                groups.put(orgName, orgConfigJson);
                consortium.put("groups", groups);
            }
        } else {
            throw new ConsortiumManageException("Orderer节点" + ordererName + "上的系统通道不存在名为" + consortiumName + "的联盟");
        }
        //计算增量，然后签名提交给orderer
        submitNewConfig(systemChainConfigBytes, configMap, ordererName, orderer.getSystemChainId());
    }

    @Override
    public void deleteConsortiumMember(String ordererName, String consortiumName, String orgName) {
        //获取系统通道配置
        OrdererInfo orderer = getOrderer(ordererName);
        byte[] systemChainConfigBytes = getSystemChainConfigBytes(orderer.getSystemChainId());
        //解码通道配置
        Map configMap = decodeChainConfig(systemChainConfigBytes);
        //修改通道配置
        Map consortiums = getConsortiums(configMap);
        if (consortiums == null) {
            throw new ConsortiumManageException("系统通道配置读取失败，配置格式不正确");
        }
        if (consortiums.containsKey(consortiumName)) {
            Map consortium = (Map) consortiums.get(consortiumName);
            if (consortium == null) {
                throw new ConsortiumManageException("获取到的联盟" + consortiumName + "配置信息为空，无法完成删除组织");
            }
            if (consortium.containsKey("groups")) {
                Map groups = (Map) consortium.get("groups");
                if (groups != null && groups.containsKey(orgName)) {
                    groups.remove(orgName);
                } else {
                    throw new ConsortiumManageException("联盟" + consortiumName + "中不存在名为" + orgName + "的组织");
                }
            }
        } else {
            throw new ConsortiumManageException("Orderer节点" + ordererName + "上的系统通道不存在名为" + consortiumName + "的联盟");
        }
        //计算增量，然后签名提交给orderer
        submitNewConfig(systemChainConfigBytes, configMap, ordererName, orderer.getSystemChainId());
    }

    private OrdererInfo getOrderer(String ordererId) {
        OrganizationInfo organizationInfo = organizationReader.getOrganizationInfo();
        if (OrganizationInfo.OrganizationType.ORDERER_ORGANIZATION != organizationInfo.getType()) {
            throw new ConsortiumManageException("只有Orderer组织才能管理联盟成员");
        }
        OrdererInfo ordererInfo = nodeReader.getOrdererInfo(ordererId);
        if (ordererInfo == null) {
            throw new ConsortiumManageException("不存在Orderer节点" + ordererId);
        }
        return ordererInfo;
    }


    private byte[] getSystemChainConfigBytes(String systemChainId) {
        try {
            return channelManager.getChannelConfigurationBytesFromOrderer(systemChainId);
        } catch (Throwable  e) {
            LOGGER.error("系统通道配置获取失败", e);
            throw new ConsortiumManageException("系统通道配置获取失败");
        }
    }

    private Map getConsortiums(Map configMap) {
        try {
            if (configMap != null && configMap.containsKey("channel_group")) {
                Map channel_group = (Map) configMap.get("channel_group");
                if (channel_group != null && channel_group.containsKey("groups")) {
                    Map groups = (Map) channel_group.get("groups");
                    if (groups != null && groups.containsKey("Consortiums")) {
                        Map consortiums = (Map) groups.get("Consortiums");
                        if (consortiums != null && consortiums.containsKey("groups")) {
                            return (Map) consortiums.get("groups");
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new ConsortiumManageException("系统通道配置读取失败，配置格式不正确");
        }
        return null;
    }

    private Map decodeChainConfig(byte[] systemChainConfigBytes) {
        try {
            String decode = configtxlator.decode(systemChainConfigBytes, ConfigTxLator.ProtoType.CONFIG);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(decode, Map.class);
        } catch (Throwable  e) {
            LOGGER.error("系统通道配置解析失败",e);
            throw new ConsortiumManageException("系统通道配置解析失败");
        }
    }

    private void submitNewConfig(byte[] original, Map modifiedMap, String ordererName, String systemChainId) {
        //编码修改后的配置
        byte[] modifiedConfigBytes;
        try {
            String configStr = new ObjectMapper().writeValueAsString(modifiedMap);
            modifiedConfigBytes = configtxlator.encode(configStr, ConfigTxLator.ProtoType.CONFIG);
        } catch (Throwable  e) {
            LOGGER.error("更新后的系统通道配置编码失败",e);
            throw new ConsortiumManageException("更新后的系统通道配置编码失败");
        }
        //计算配置增量
        byte[] updatedConfigBytes;
        try {
            updatedConfigBytes = configtxlator.computeUpdate(original, modifiedConfigBytes, systemChainId);
        } catch (Throwable  e) {
            LOGGER.error("更新后的系统通道配置编码失败", e);
            throw new ConsortiumManageException("通道配置更新增量计算失败");
        }
        //签名配置交易
        byte[] signature;
        try {
            signature = channelManager.getUpdateChannelConfigurationSignature(updatedConfigBytes);
        } catch (Throwable  e) {
            LOGGER.error("更新后的系统通道配置编码失败", e);
            throw new ConsortiumManageException("通道配置交易签名失败");
        }
        //提交给orderer节点
        try {
            channelManager.submitChannelConfig(systemChainId, updatedConfigBytes, signature);
        } catch (Throwable  e) {
            LOGGER.error("更新后的系统通道配置编码失败", e);
            throw new ConsortiumManageException("通道配置更新交易提交失败");
        }
    }
}
