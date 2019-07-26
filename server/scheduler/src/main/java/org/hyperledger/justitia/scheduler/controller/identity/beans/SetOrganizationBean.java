package org.hyperledger.justitia.scheduler.controller.identity.beans;

import lombok.Data;
import org.hyperledger.justitia.service.face.identity.bean.OrganizationInfo;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SetOrganizationBean {
    private String name;
    private String type;
    private String mspId;
    private Boolean tlsEnable;
    private MultipartFile caCert;
    private MultipartFile tlsCaCert;
}
