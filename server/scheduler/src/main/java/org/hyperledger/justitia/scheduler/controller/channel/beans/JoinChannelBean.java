package org.hyperledger.justitia.scheduler.controller.channel.beans;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class JoinChannelBean {
    @NotEmpty
    private String channelId;
    @NotEmpty
    private List<String> peerId;
}
