package org.hyperledger.justitia.scheduler.controller.identity.beans;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class MspBean {
    private List<MultipartFile> adminCerts;
    private MultipartFile caCerts;
    private List<MultipartFile> intermediateCerts;
    private List<MultipartFile> crls;
    private MultipartFile configFile;      //config.yaml
    private MultipartFile keyStore;
    private MultipartFile signCerts;
    private MultipartFile tlsCaCerts;
    private List<MultipartFile> tlsIntermediateCerts;
}
