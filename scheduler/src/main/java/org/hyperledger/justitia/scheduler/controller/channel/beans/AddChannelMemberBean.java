package org.hyperledger.justitia.scheduler.controller.channel.beans;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddChannelMemberBean {
    private String channelId;
    private String description;
    private String orgName;
    private MultipartFile orgConfig;
}
