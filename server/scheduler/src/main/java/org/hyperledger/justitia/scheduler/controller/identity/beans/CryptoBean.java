package org.hyperledger.justitia.scheduler.controller.identity.beans;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class CryptoBean {
    //ca
    private MultipartFile caCert;
    private MultipartFile caKey;

    //tls
    private MultipartFile tlsCaCert;
    private MultipartFile tlsCaKey;
    private MultipartFile tlsCert;
    private MultipartFile tlsKey;

    //sign
    private MultipartFile signCerts;
    private MultipartFile signKey;



//    private List<MultipartFile> adminCerts;
//    private MultipartFile caCerts;
//    private List<MultipartFile> intermediateCerts;
//    private List<MultipartFile> crls;
//    private MultipartFile configFile;      //config.yaml
//    private MultipartFile keyStore;
//    private MultipartFile signCerts;
//    private MultipartFile tlsCaCerts;
//    private List<MultipartFile> tlsIntermediateCerts;
}
