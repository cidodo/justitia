package org.hyperledger.justitia.identity.service.beans.crypto;

import lombok.Data;
import org.hyperledger.justitia.identity.service.beans.NodeInfo;
import org.hyperledger.justitia.identity.service.beans.FabricUserInfo;

import java.util.List;

@Data
public class OrganizationCrypto {
    private CaInfo ca;
    private MspInfo msp;
    private List<NodeInfo> nodes;
    private CaInfo tlsca;
    private List<FabricUserInfo> users;
}
