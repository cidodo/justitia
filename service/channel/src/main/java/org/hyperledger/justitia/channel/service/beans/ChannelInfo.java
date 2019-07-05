package org.hyperledger.justitia.channel.service.beans;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ChannelInfo {
    @Getter private final String channelId;
    @Getter private List<String> peers;
    @Getter private List<OrgInfo> orgs;

    public ChannelInfo(String channelId) {
        this.channelId = channelId;
        peers = new ArrayList<>();
        orgs = new ArrayList<>();
    }

    public void addPeer(String peerName) {
        peers.add(peerName);
    }

    public void addOrg(OrgInfo orgInfo) {
        orgs.add(orgInfo);
    }

    public static class OrgInfo {
        @Getter private final String name;
        @Getter private final String mspId;
        @Getter private final List<String> anchorPeers;

        public OrgInfo(String name, String mspId, List<String> anchorPeers) {
            this.name = name;
            this.mspId = mspId;
            this.anchorPeers = anchorPeers;
        }
    }
}
