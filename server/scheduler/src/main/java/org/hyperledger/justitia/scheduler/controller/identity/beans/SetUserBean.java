package org.hyperledger.justitia.scheduler.controller.identity.beans;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class SetUserBean extends CryptoBean {
    private String userName;
    private Boolean admin;

    //tlsInfo
    private Boolean tlsEnable;
    private List<String> hostsName;
}
