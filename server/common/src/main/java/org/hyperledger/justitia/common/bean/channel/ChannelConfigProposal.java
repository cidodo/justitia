package org.hyperledger.justitia.common.bean.channel;

import lombok.Data;

import java.util.Date;

@Data
public class ChannelConfigProposal {
    private String channelId;
    private String requester;
    private String proposalId;
    private Long channelConfigVersion;
    private String status;
    private Date requestTime;
    private String requestType;
    private Boolean reject;
    private String reason;
    private Date responseTime;
    private byte[] content;
    private String description;
    private String expectedEndorsement;

}
