package org.hyperledger.justitia.channel.service.beans;

import lombok.Data;

import java.util.Date;

@Data
public class TaskInfo {
    private String channelId;
    private String requestId;
    private String requester;
    private String requesterType;
    private String description;
    private String status;
    private String content;
    private Date date;
}
