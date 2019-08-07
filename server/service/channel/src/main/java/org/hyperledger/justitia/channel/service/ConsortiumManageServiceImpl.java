package org.hyperledger.justitia.channel.service;

import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.justitia.channel.exception.ChannelServiceException;
import org.hyperledger.justitia.channel.service.member.ConsortiumMemberService;
import org.hyperledger.justitia.common.bean.channel.ChannelMember;
import org.hyperledger.justitia.common.bean.identity.Organization;
import org.hyperledger.justitia.common.bean.node.OrdererInfo;
import org.hyperledger.justitia.common.face.service.channel.ConsortiumManageService;
import org.hyperledger.justitia.common.bean.channel.ConsortiumInfo;
import org.hyperledger.justitia.common.face.service.identity.NodeService;
import org.hyperledger.justitia.common.face.service.identity.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConsortiumManageServiceImpl implements ConsortiumManageService {
    private final ConsortiumMemberService consortiumMemberService;
    private final OrganizationService organizationService;
    private final NodeService nodeService;

    @Autowired
    public ConsortiumManageServiceImpl(ConsortiumMemberService consortiumMemberService, OrganizationService organizationService, NodeService nodeService) {
        this.consortiumMemberService = consortiumMemberService;
        this.organizationService = organizationService;
        this.nodeService = nodeService;
    }

    private String getSystemChainId(String ordererId) {
        Organization organizationInfo = organizationService.getOrganization();
        if (Organization.OrganizationType.ORDERER_ORGANIZATION != organizationInfo.getType()) {
            throw new ChannelServiceException(ChannelServiceException.ORGANIZATION_TYPE_ERROR, organizationInfo.getType());
        }
        OrdererInfo ordererInfo = nodeService.getOrdererInfo(ordererId);
        if (ordererInfo == null) {
            throw new ChannelServiceException(ChannelServiceException.ORDERER_DOES_NOT_EXITS, ordererId);
        }
        return ordererInfo.getSystemChainId();
    }

    private Configtx.Config getSystemChainConfig(String ordererId) {
        String systemChainId = getSystemChainId(ordererId);
        return consortiumMemberService.getChainConfigAsConfig(systemChainId);
    }

    @Override
    public List<ConsortiumInfo> getConsortiums(String ordererId) {
        Configtx.Config systemChainConfig = getSystemChainConfig(ordererId);
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
    public void addConsortiumMember(String ordererId, String consortiumName, ChannelMember member) {
        String systemChainId = getSystemChainId(ordererId);
        consortiumMemberService.addConsortiumMember(systemChainId, consortiumName, member);
    }

    @Override
    public void updateConsortiumMember(String ordererId, String consortiumName, ChannelMember member) {
        String systemChainId = getSystemChainId(ordererId);
        consortiumMemberService.updateConsortiumMember(systemChainId, consortiumName, member);
    }

    @Override
    public void deleteConsortiumMember(String ordererId, String consortiumName, String memberName) {
        String systemChainId = getSystemChainId(ordererId);
        consortiumMemberService.deleteConsortiumMember(systemChainId, consortiumName, memberName);
    }
}
