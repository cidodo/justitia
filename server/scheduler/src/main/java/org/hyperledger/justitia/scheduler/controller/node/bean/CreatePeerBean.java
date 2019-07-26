package org.hyperledger.justitia.scheduler.controller.node.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hyperledger.justitia.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreatePeerBean extends CreateContainerBean {
    @NotEmpty
    private String peerName;
    private int serverPort;

    /**
     * couchdb
     */
    private Boolean couchdbEnable;
    private String couchdbImage;
    private String couchdbTag;
    private String couchdbContainerName;
    private Integer couchdbExposedPort;

    /**
     * mspInfo
     */
    @NotEmpty
    private String caServerName;
    @NotEmpty
    private String peerUserId;

    @Autowired
    public String getImage() {
        if (StringUtils.isEmpty(image)) {
            image = "hyperledger/fabric-peer";
        }
        return image;
    }
}
