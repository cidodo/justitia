package org.hyperledger.justitia.service.face.channel.bean;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ChannelInfo {
    @Getter private final String channelId;
    @Getter private List<String> peers;
    @Getter private List<Member> members;

    public ChannelInfo(String channelId) {
        this.channelId = channelId;
        peers = new ArrayList<>();
        members = new ArrayList<>();
    }

    public void addPeer(String peerName) {
        peers.add(peerName);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public static class Member {
        @Getter private final String name;
        @Getter private final String mspId;
        @Getter private final List<String> anchorPeers;

        public Member(String name, String mspId, List<String> anchorPeers) {
            this.name = name;
            this.mspId = mspId;
            this.anchorPeers = anchorPeers;
        }
    }
}
