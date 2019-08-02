package org.hyperledger.justitia.channel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.justitia.common.bean.channel.ChannelMember;
import org.hyperledger.justitia.common.face.service.channel.ConsortiumManageService;
import org.hyperledger.justitia.common.bean.channel.ConsortiumInfo;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.hyperledger.justitia.common.face.service.fabric.FabricToolsService;
import org.hyperledger.justitia.common.face.service.identity.NodeService;
import org.hyperledger.justitia.common.face.service.identity.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConsortiumManageServiceImpl extends ChannelConfigService implements ConsortiumManageService{
    private final OrganizationService organizationService;
    private final NodeService nodeService;

    @Autowired
    public ConsortiumManageServiceImpl(OrganizationService organizationService,NodeService nodeService,
                                       ChannelService channelService, FabricToolsService fabricToolsService) {
        super(channelService, fabricToolsService);
        this.nodeService = nodeService;
        this.organizationService = organizationService;
    }

    private String getSystemChainId(String ordererId) {
        OrganizationInfo organizationInfo = organizationService.getOrganizationInfo();
        if (OrganizationInfo.OrganizationType.ORDERER_ORGANIZATION != organizationInfo.getType()) {
            throw new ConsortiumManageException("只有Orderer组织才能管理联盟成员");
        }
        OrdererInfo ordererInfo = nodeService.getOrdererInfo(ordererId);
        if (ordererInfo == null) {
            throw new ConsortiumManageException("不存在Orderer节点" + ordererId);
        }
        return ordererInfo.getSystemChainId();
    }

    @Override
    public List<ConsortiumInfo> getConsortiums(String ordererId) {
        byte[] systemChainConfigBytes = getChainConfigBytes(getSystemChainId(ordererId));
        Configtx.Config systemChainConfig = parseChainConfig(systemChainConfigBytes);
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
    public void addConsortiumMember(String ordererId, String consortiumName, String memberName, String memberConfigStr) {
        String systemChainId = getSystemChainId(ordererId);
        JsonNode memberConfig = new ObjectMapper().readTree(memberConfigStr);

        byte[] original = getChainConfigBytes(systemChainId);
        JsonNode baseConfig = decodeChainConfig(original);
        addChainConfig(baseConfig, "", memberName, memberConfig);
        byte[] updated = encodeChainConfig(baseConfig);
        byte[] updateConfigTrans = computeUpdateChainConfig(original, updated, systemChainId);

        submitConfigUpdate(systemChainId, updateConfigTrans, null);
    }

    public void updateConsortiumMember(String ordererId, String consortiumName,  ChannelMember member) {
        String systemChainId = getSystemChainId(ordererId);
        JsonNode memberConfig = new ObjectMapper().readTree(memberConfigStr);

        byte[] original = getChainConfigBytes(systemChainId);
        JsonNode baseConfig = decodeChainConfig(original);
        updateChainConfig(baseConfig, "", memberName, memberConfig);
        byte[] updated = encodeChainConfig(baseConfig);
        byte[] updateConfigTrans = computeUpdateChainConfig(original, updated, systemChainId);

        submitConfigUpdate(systemChainId, updateConfigTrans, null);
    }

    @Override
    public void deleteConsortiumMember(String ordererId, String consortiumName, String memberName) {
        String systemChainId = getSystemChainId(ordererId);

        byte[] original = getChainConfigBytes(systemChainId);
        JsonNode baseConfig = decodeChainConfig(original);
        deleteChainConfig(baseConfig, "");
        byte[] updated = encodeChainConfig(baseConfig);
        byte[] updateConfigTrans = computeUpdateChainConfig(original, updated, systemChainId);

        submitConfigUpdate(systemChainId, updateConfigTrans, null);
    }
}
