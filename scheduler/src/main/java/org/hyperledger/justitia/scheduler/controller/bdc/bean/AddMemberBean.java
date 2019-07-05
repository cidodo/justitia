package org.hyperledger.justitia.scheduler.controller.bdc.bean;

import lombok.Data;

@Data
public class AddMemberBean {
    private String channelId;
    private String orgName;
    private String orgMspId;
    private String description;
    private String identity;
    private int userCount;
    private int peerCount;
}
