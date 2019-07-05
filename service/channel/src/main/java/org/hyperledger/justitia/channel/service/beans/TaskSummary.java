package org.hyperledger.justitia.channel.service.beans;

import lombok.Data;

@Data
public class TaskSummary {
    private String taskId;
    private String requester;
    private String description;
    private String status;
    private boolean owner;
}
