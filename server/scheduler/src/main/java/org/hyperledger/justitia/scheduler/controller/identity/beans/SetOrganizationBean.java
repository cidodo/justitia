package org.hyperledger.justitia.scheduler.controller.identity.beans;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SetOrganizationBean extends CryptoBean{
    private String name;
    private String type;
    private String mspId;
    private Boolean tlsEnable;
}
