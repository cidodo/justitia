package org.hyperledger.justitia.service.face.identity.bean.crypto;

import lombok.Data;
import org.hyperledger.justitia.service.face.identity.bean.FabricUserInfo;

import java.util.List;

@Data
public class MspInfo {
    private List<FabricUserInfo> adminCerts;
    private String caCerts;
    private List<String> intermediateCerts;
    private List<String> crls;
    private String configFile;      //config.yaml
    private String keyStore;
    private String signCerts;
    private String tlsCaCerts;
    private List<String> tlsIntermediateCerts;
}
