package org.hyperledger.justitia.channel.service.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.justitia.channel.exception.MemberManageException;
import org.hyperledger.justitia.farbic.sdk.ChannelManager;
import org.hyperledger.justitia.farbic.tools.ConfigTxLator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MemberManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberManager.class);

    private final ChannelManager channelManager;
    private final ConfigTxLator configtxlator;
    private final MemberManageChaincode memberManageChaincode;


    @Autowired
    public MemberManager(ChannelManager channelManager, ConfigTxLator configtxlator, MemberManageChaincode memberManageChaincode) {
        this.channelManager = channelManager;
        this.configtxlator = configtxlator;
        this.memberManageChaincode = memberManageChaincode;
    }

    public String updateOrgConfig(String channelName, String orgName, String description, OrgConfigModifyBean modifyBean) {
        //todo 根据modifyBean修改组织配置,获得更新后的json配置
        String orgConfig = modifyOrgConfig(null, modifyBean);
        byte[] transaction = createTransaction(channelName, orgName, orgConfig, MemberManageChaincode.RequestType.MODIFY_ORG_CONFIG);
        submitBySelfSign(channelName, transaction);
        return null;
    }

    public String addOrganization(String channelName, String orgName, String orgConfig, String description) {
        byte[] transaction = createTransaction(channelName, orgName, orgConfig, MemberManageChaincode.RequestType.ADD_MEMBER);
        submitBySelfSign(channelName, transaction);
        return null;
    }

    public String deleteOrganization(String channelName, String orgName, String description) {
        byte[] transaction = createTransaction(channelName, orgName, null, MemberManageChaincode.RequestType.DELETE_MEMBER);
        submitBySelfSign(channelName, transaction);
        return null;
    }

    private byte[] createTransaction(String channelName, String orgName, String orgConfig, MemberManageChaincode.RequestType type) {
        Map orgConfigJson = null;
        if (MemberManageChaincode.RequestType.ADD_MEMBER == type || MemberManageChaincode.RequestType.MODIFY_ORG_CONFIG == type) {
            try {
                orgConfigJson = new ObjectMapper().readValue(orgConfig, Map.class);
            } catch (Exception e) {
                throw new MemberManageException("组织配置数据不是一个json格式");
            }
        }

        byte[] channelConfigBytes = getChannelConfigBytes(channelName);
        Map config = decodeChainConfig(channelConfigBytes);
        if (!config.containsKey("sequence")) {
            throw new MemberManageException("通道配置读取失败，配置格式不正确");
        }
        Map groups = getApplicationGroups(config);
        if (groups == null) {
            throw new MemberManageException("通道配置读取失败，配置格式不正确");
        }
        switch (type) {
            case ADD_MEMBER:
                if (groups.containsKey(orgName)) {
                    throw new MemberManageException("通道" + channelName + "中已存在名为" + orgName + "的组织");
                } else {
                    groups.put(orgName, orgConfigJson);
                }
                break;
            case DELETE_MEMBER:
                if (groups.containsKey(orgName)) {
                    groups.remove(orgName);
                } else {
                    throw new MemberManageException("通道" + channelName + "中不包含名为" + orgName + "的组织");
                }
                break;
            case MODIFY_ORG_CONFIG:
                if (groups.containsKey(orgName)) {
                    groups.replace(orgName, orgConfigJson);
                } else {
                    throw new MemberManageException("通道" + channelName + "中不包含名为" + orgName + "的组织");
                }
                break;
            default:
                throw new MemberManageException("未知的请求类型：" + type);
        }
        return createTransaction(channelConfigBytes, config, channelName);
    }

    private void submitBySelfSign(String channelId, byte[] updatedConfigBytes) {
        byte[] signature;
        //签名配置交易
        try {
            signature = channelManager.getUpdateChannelConfigurationSignature(updatedConfigBytes);
        } catch (Exception e) {
            LOGGER.error("通道配置交易签名失败", e);
            throw new MemberManageException("通道配置交易签名失败");
        }
        //提交交易到orderer节点
        try {
            channelManager.submitChannelConfig(channelId, updatedConfigBytes, signature);
        } catch (Exception e) {
            LOGGER.error("通道配置更新交易提交失败", e);
            throw new MemberManageException("通道配置更新交易提交失败");
        }
    }

    private byte[] getChannelConfigBytes(String channelName) {
        try {
            return channelManager.getChannelConfigurationBytes(channelName);
        } catch (Exception e) {
            LOGGER.error("系统通道配置获取失败", e);
            throw new MemberManageException("系统通道配置获取失败");
        }
    }

    private Map decodeChainConfig(byte[] systemChainConfigBytes) {
        try {
            String decode = configtxlator.decode(systemChainConfigBytes, ConfigTxLator.ProtoType.CONFIG);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(decode, Map.class);
        } catch (Exception e) {
            LOGGER.error("通道配置解析失败", e);
            throw new MemberManageException("通道配置解析失败");
        }
    }

    private Map getApplicationGroups(Map channelConfig) {
        if (channelConfig == null) {
            throw new MemberManageException("通道配置为空");
        }

        try {
            if (channelConfig.containsKey("channel_group")) {
                Map channel_group = (Map) channelConfig.get("channel_group");
                if (channel_group != null && channel_group.containsKey("groups")) {
                    Map groups = (Map) channel_group.get("groups");
                    if (groups != null && groups.containsKey("Application")) {
                        Map application = (Map) groups.get("Application");
                        if (application != null && application.containsKey("groups")) {
                            return (Map) application.get("groups");
                        }
                    }
                }

            }
        } catch (Exception e) {
            throw new MemberManageException("系统通道配置读取失败，配置格式不正确");
        }
        return null;
    }

    private String modifyOrgConfig(Map orgConfig, OrgConfigModifyBean modifyBean) {
        return null;
    }

    private byte[] createTransaction(byte[] original, Map modifiedMap, String channelId) {
        //编码修改后的配置
        byte[] modifiedConfigBytes;
        try {
            String configStr = new ObjectMapper().writeValueAsString(modifiedMap);
            modifiedConfigBytes = configtxlator.encode(configStr, ConfigTxLator.ProtoType.CONFIG);
        } catch (Exception e) {
            LOGGER.error("更新后的通道配置编码失败", e);
            throw new MemberManageException("更新后的通道配置编码失败");
        }
        //计算配置增量
        try {
            return configtxlator.computeUpdate(original, modifiedConfigBytes, channelId);
        } catch (Exception e) {
            LOGGER.error("通道配置更新增量计算失败", e);
            throw new MemberManageException("通道配置更新增量计算失败");
        }
    }

    public void channelConfigTaskResponse(String channelId, String requestId, String requester, byte[] updatedConfigBytes, boolean reject, String reason) {
        byte[] signature;
        try {
            signature = channelManager.getUpdateChannelConfigurationSignature(updatedConfigBytes);
        } catch (Exception e) {
            LOGGER.error("更新通道配置交易签名失败", e);
            throw new MemberManageException("更新通道配置交易签名失败");
        }
        try {
            memberManageChaincode.signResponses(channelId, requestId, requester, reject, signature, reason);
        } catch (MemberManageException e) {
            LOGGER.error("任务响应失败.", e);
            throw new MemberManageException("任务响应失败.");
        }
    }

    public void submitChannelConfigToOrderer(String channelId, byte[] updatedConfigBytes, String requestId) {
        ArrayList<byte[]> signatures = new ArrayList<>();
        List<MemberManageChaincode.SignResponse> signResponses;
        try {
            signResponses = memberManageChaincode.getAllSignResponses(channelId, requestId);
        } catch (MemberManageException e) {
            LOGGER.error("获取其他通道成员的签名数据失败", e);
            throw new MemberManageException("获取其他通道成员的签名数据失败");
        }
        for (MemberManageChaincode.SignResponse response :signResponses) {
            signatures.add(Base64.getDecoder().decode(response.getSignature()));
        }

        //签名配置交易
        try {
            byte[] selfSignature = channelManager.getUpdateChannelConfigurationSignature(updatedConfigBytes);
            signatures.add(selfSignature);
        } catch (Exception e) {
            LOGGER.error("通道配置交易签名失败", e);
            throw new MemberManageException("通道配置交易签名失败");
        }
        //提交交易到orderer节点
        try {
            channelManager.submitChannelConfig(channelId, updatedConfigBytes, signatures.toArray(new byte[signatures.size()][]));
        } catch (Exception e) {
            LOGGER.error("通道配置更新交易提交失败", e);
            throw new MemberManageException("通道配置更新交易提交失败");
        }
    }

}
