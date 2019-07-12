package org.hyperledger.justitia.common.face.modules.identity.beans.crypto;

import lombok.Data;
import org.hyperledger.justitia.common.face.modules.identity.beans.FabricUserInfo;

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
