package org.hyperledger.justitia.service.face.fabric.bean;

import lombok.Getter;

import java.util.List;

public class ChannelMember {
    @Getter private String name;
    @Getter private String mspId;
    @Getter private List<String> anchorPeers;

    public ChannelMember(String name, String mspId, List<String> anchorPeers) {
        this.name = name;
        this.mspId = mspId;
        this.anchorPeers = anchorPeers;
    }
}
