package org.hyperledger.justitia.scheduler.controller.channel.beans;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteConsortiumMemberBean {
    @NotEmpty
    private String ordererName;
    @NotEmpty
    private String consortium;
    @NotEmpty
    private String orgName;
}
