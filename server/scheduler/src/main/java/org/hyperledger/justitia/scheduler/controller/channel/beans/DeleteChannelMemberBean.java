package org.hyperledger.justitia.scheduler.controller.channel.beans;

import lombok.Data;

@Data
public class DeleteChannelMemberBean {
    private String channelId;
    private String orgName;
    private String description;
}
