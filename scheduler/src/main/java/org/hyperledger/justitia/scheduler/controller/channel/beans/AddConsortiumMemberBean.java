package org.hyperledger.justitia.scheduler.controller.channel.beans;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddConsortiumMemberBean {
    @NotEmpty
    private String ordererName;
    @NotEmpty
    private String consortiumName;
    @NotEmpty
    private String orgName;
    @NotNull
    private MultipartFile orgConfig;
}
