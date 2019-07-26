package org.hyperledger.justitia.farbic.data;

import org.hyperledger.justitia.service.face.fabric.NetworkService;
import org.hyperledger.justitia.service.face.fabric.bean.ChannelMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class NetworkServiceImpl implements NetworkService {
    private final SyncData4Chain syncData4Chain;

    @Autowired
    public NetworkServiceImpl(SyncData4Chain syncData4Chain) {
        this.syncData4Chain = syncData4Chain;
    }

    @Override
    public Set<String> getPeersId(String channelId) {
        return NetworkConfig.getPeersId(channelId);
    }

    @Override
    public Set<String> getChannelsId() {
        return NetworkConfig.getChannelsId();
    }

    @Override
    public Set<String> getChannelsIdByPeer(String peerId) {
        return NetworkConfig.getChannelsIdByPeer(peerId);
    }

    @Override
    public Map<String, List<ChannelMember>> getChannelsMember() {
        return NetworkConfig.getChannelRefOrg();
    }

    @Override
    public List<ChannelMember> getChannelMembers(String channelId) {
        return NetworkConfig.getOrganizations(channelId);
    }

    @Override
    public Set<String> getChannelsIdByOrganization(String organizationName) {
        return NetworkConfig.getChannelsIdByOrganization(organizationName);
    }

    @Override
    public void resetNetwork() {
        //Fixme Try to find a more appropriate method
        syncData4Chain.syncData();
    }


}
