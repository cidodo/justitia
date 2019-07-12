package org.hyperledger.justitia.farbic.data;

import org.hyperledger.justitia.common.face.modules.fabric.ChainDataService;
import org.hyperledger.justitia.common.face.modules.fabric.bean.ChannelMember;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChainDataServiceImpl implements ChainDataService {
    @Override
    public Set<String> getPeersId(String channelId) {
        return PeerRefChannel.getPeersId(channelId);
    }

    @Override
    public Set<String> getAllChannelsId() {
        return PeerRefChannel.getAllChannelsId();
    }

    @Override
    public Set<String> getChannelsIdByPeer(String peerId) {
        return PeerRefChannel.getChannelsId(peerId);
    }

    @Override
    public Map<String, List<ChannelMember>> getChannelRefOrg() {
        return ChannelRefOrganization.getChannelRefOrg();
    }

    @Override
    public List<ChannelMember> getOrganizations(String channelId) {
        return ChannelRefOrganization.getOrganizations(channelId);
    }

    @Override
    public Set<String> getChannelsIdByOrg(String organizationName) {
        return ChannelRefOrganization.getChannelsId(organizationName);
    }
}
