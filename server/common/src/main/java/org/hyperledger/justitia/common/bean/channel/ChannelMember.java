package org.hyperledger.justitia.common.bean.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

public class ChannelMember {
    @Getter private String name;
    @Getter private String mspId;
    @Getter private JsonNode memberConfig;
    @Getter private List<String> anchorPeers;

    public ChannelMember(String name, String mspId, List<String> anchorPeers) {
        this.name = name;
        this.mspId = mspId;
        this.anchorPeers = anchorPeers;
    }

    public void setMemberConfig(String memberConfigStr) throws IOException {
        this.memberConfig = new ObjectMapper().readTree(memberConfigStr);
    }
}
