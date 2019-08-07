package org.hyperledger.justitia.common.bean.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Getter
public class ChannelMember {
    private String name;
    @Setter private String mspId;
    @Setter private JsonNode memberConfig;
    @Setter private Set<String> anchorPeers;

    public ChannelMember(String name) {
        this.name = name;
    }

    public void setMemberConfig(String memberConfigStr) throws IOException {
        this.memberConfig = new ObjectMapper().readTree(memberConfigStr);
    }
}
