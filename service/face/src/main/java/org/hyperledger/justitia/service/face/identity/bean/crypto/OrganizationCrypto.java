package org.hyperledger.justitia.service.face.identity.bean.crypto;

import lombok.Data;
import org.hyperledger.justitia.service.face.identity.bean.NodeInfo;
import org.hyperledger.justitia.service.face.identity.bean.FabricUserInfo;

import java.util.List;

@Data
public class OrganizationCrypto {
    private CaInfo ca;
    private MspInfo msp;
    private List<NodeInfo> nodes;
    private CaInfo tlsca;
    private List<FabricUserInfo> users;
}
