package org.hyperledger.justitia.scheduler.controller.channel.beans;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ResponseTaskBean {
    @NotEmpty
    private String taskId;
    @NotNull
    private Boolean reject;
    private String reason;
}
