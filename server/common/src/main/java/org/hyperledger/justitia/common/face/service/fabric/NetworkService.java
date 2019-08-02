package org.hyperledger.justitia.common.face.service.fabric;

import org.hyperledger.justitia.common.bean.channel.ChannelMember;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NetworkService {
    Set<String> getPeersId(String channelId);
    Set<String> getChannelsId();
    Set<String> getChannelsIdByPeer(String peerId);
    Map<String, List<ChannelMember>> getChannelsMember();
    List<ChannelMember> getChannelMembers(String channelId);
    Set<String> getChannelsIdByOrganization(String organizationName);

    void resetNetwork();
}
