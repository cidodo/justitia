package org.hyperledger.justitia.common.face.modules.fabric;


import org.hyperledger.justitia.common.face.modules.fabric.bean.ChannelMember;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ChainDataService {
    Set<String> getPeersId(String channelId);
    Set<String> getAllChannelsId();
    Set<String> getChannelsIdByPeer(String peerId);
    Map<String, List<ChannelMember>> getChannelsMember();
    List<ChannelMember> getOrganizations(String channelId);
    Set<String> getChannelsIdByOrg(String organizationName);
}
