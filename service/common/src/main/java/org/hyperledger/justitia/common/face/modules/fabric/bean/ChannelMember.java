package org.hyperledger.justitia.common.face.modules.fabric.bean;

import lombok.Getter;

import java.util.List;

public class ChannelMember {
    @Getter private String name;
    @Getter private String mspId;
    @Getter private List<String> anchorPeers;

    ChannelMember(String name, String mspId, List<String> anchorPeers) {
        this.name = name;
        this.mspId = mspId;
        this.anchorPeers = anchorPeers;
    }
}
