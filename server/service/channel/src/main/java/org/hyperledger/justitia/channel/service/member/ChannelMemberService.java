package org.hyperledger.justitia.channel.service.member;

import com.fasterxml.jackson.databind.JsonNode;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.justitia.channel.service.member.MemberManageChaincode.RequestType;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.hyperledger.justitia.common.face.service.fabric.FabricToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class ChannelMemberService extends ChannelConfigService {
    private final static String APPLICATION_GROUPS = "channel_group.groups.Application.groups";


    @Autowired
    public ChannelMemberService(ChannelService channelService, FabricToolsService fabricToolsService) {
        super(channelService, fabricToolsService);
    }

    public CMSCCRequestBean addMember(String channelId, String memberName, JsonNode memberConfig, String description) {
        byte[] original = getChainConfigBytes(channelId);
        JsonNode configJson = decodeChainConfig(original);
        addChainConfig(channelId, configJson, APPLICATION_GROUPS, memberName, memberConfig);
        byte[] updated = encodeChainConfig(configJson);
        byte[] updatedConfigTransaction = computeUpdateChainConfig(original, updated, channelId);
        return generateRequestBean(channelId, original, updatedConfigTransaction, description, RequestType.ADD_MEMBER);
    }

    public CMSCCRequestBean deleteMember(String channelId, String memberName, String description) {
        byte[] original = getChainConfigBytes(channelId);
        JsonNode configJson = decodeChainConfig(original);
        deleteChainConfig(channelId, configJson, APPLICATION_GROUPS, memberName);
        byte[] updated = encodeChainConfig(configJson);
        byte[] updatedConfigTransaction = computeUpdateChainConfig(original, updated, channelId);
        return generateRequestBean(channelId, original, updatedConfigTransaction, description, RequestType.DELETE_MEMBER);
    }

    public CMSCCRequestBean updateMember(String channelId, String memberName, JsonNode memberConfig, String description) {
        byte[] original = getChainConfigBytes(channelId);
        JsonNode configJson = decodeChainConfig(original);
        updateChainConfig(channelId, configJson, APPLICATION_GROUPS, memberName, memberConfig);
        byte[] updated = encodeChainConfig(configJson);
        byte[] updatedConfigTransaction = computeUpdateChainConfig(original, updated, channelId);

        return generateRequestBean(channelId, original, updatedConfigTransaction, description, RequestType.UPDATE_MEMBER);
    }

    private CMSCCRequestBean generateRequestBean(String channelId, byte[] original, byte[] updatedConfigTransaction,
                                                 String description, RequestType requestType) {
        Configtx.Config originalConfig = parseChainConfig(original);
        long sequence = getConfigSequence(originalConfig);
        Set<String> membersMspId = getMembersMspId(originalConfig);
        CMSCCRequestBean proposalInfo = new CMSCCRequestBean(channelId);
        proposalInfo.setUpdatedConfigTrans(updatedConfigTransaction);
        proposalInfo.setRequestType(requestType);
        proposalInfo.setSequence(sequence);
        proposalInfo.setDescription(description);
        proposalInfo.setMembersMspId(membersMspId);
        return proposalInfo;
    }
}
