package org.hyperledger.justitia.common.bean.channel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ChannelInfo {
    @Getter private final String channelId;
    @Getter @Setter private String consortium;
    @Getter @Setter private List<String> peers;
    @Getter @Setter private List<ChannelMember> members;

    public ChannelInfo(String channelId) {
        this.channelId = channelId;
        peers = new ArrayList<>();
        members = new ArrayList<>();
    }

    public void addPeer(String peerName) {
        peers.add(peerName);
    }

    public void addMember(ChannelMember member) {
        members.add(member);
    }
}
