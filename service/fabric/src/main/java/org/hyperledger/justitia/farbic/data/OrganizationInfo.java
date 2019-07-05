package org.hyperledger.justitia.farbic.data;

import lombok.Getter;

import java.util.List;

public class OrganizationInfo {
    @Getter private String name;
    @Getter private String mspId;
    @Getter private List<String> anchorPeers;

    OrganizationInfo(String name, String mspId, List<String> anchorPeers) {
        this.name = name;
        this.mspId = mspId;
        this.anchorPeers = anchorPeers;
    }
}
