package org.hyperledger.justitia.channel.service.member;

import com.fasterxml.jackson.databind.JsonNode;
import org.hyperledger.justitia.common.bean.channel.ChannelMember;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.hyperledger.justitia.common.face.service.fabric.FabricToolsService;

public class ConsortiumMemberService extends ChannelConfigService {
    private final static String CONSORTIUM_GROUPS = "channel_group.groups.Consortiums.groups";


    public ConsortiumMemberService(ChannelService channelService, FabricToolsService fabricToolsService) {
        super(channelService, fabricToolsService);
    }


    private String getConsortiumGroupsPath(String consortiumName) {
        return CONSORTIUM_GROUPS + "." + consortiumName + ".groups";
    }

    public void addConsortiumMember(String systemChainId, String consortiumName, ChannelMember member) {
        byte[] original = getChainConfigBytes(systemChainId);
        JsonNode baseConfig = decodeChainConfig(original);
        addChainConfig(systemChainId, baseConfig, getConsortiumGroupsPath(consortiumName), member.getName(), member.getMemberConfig());
        byte[] updated = encodeChainConfig(baseConfig);
        byte[] updateConfigTrans = computeUpdateChainConfig(original, updated, systemChainId);

        submitConfigUpdate(systemChainId, updateConfigTrans, null);
    }

    public void updateConsortiumMember(String systemChainId, String consortiumName, ChannelMember member) {
        byte[] original = getChainConfigBytes(systemChainId);
        JsonNode baseConfig = decodeChainConfig(original);
        updateChainConfig(systemChainId, baseConfig, getConsortiumGroupsPath(consortiumName), member.getName(), member.getMemberConfig());
        byte[] updated = encodeChainConfig(baseConfig);
        byte[] updateConfigTrans = computeUpdateChainConfig(original, updated, systemChainId);

        submitConfigUpdate(systemChainId, updateConfigTrans, null);
    }

    public void deleteConsortiumMember(String systemChainId, String consortiumName, String memberName) {
        byte[] original = getChainConfigBytes(systemChainId);
        JsonNode baseConfig = decodeChainConfig(original);
        deleteChainConfig(systemChainId, baseConfig, getConsortiumGroupsPath(consortiumName), memberName);
        byte[] updated = encodeChainConfig(baseConfig);
        byte[] updateConfigTrans = computeUpdateChainConfig(original, updated, systemChainId);

        submitConfigUpdate(systemChainId, updateConfigTrans, null);
    }
}
