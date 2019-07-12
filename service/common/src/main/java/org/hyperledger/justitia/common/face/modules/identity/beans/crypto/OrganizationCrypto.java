package org.hyperledger.justitia.common.face.modules.identity.beans.crypto;

import lombok.Data;
import org.hyperledger.justitia.common.face.modules.identity.beans.NodeInfo;
import org.hyperledger.justitia.common.face.modules.identity.beans.FabricUserInfo;

import java.util.List;

@Data
public class OrganizationCrypto {
    private CaInfo ca;
    private MspInfo msp;
    private List<NodeInfo> nodes;
    private CaInfo tlsca;
    private List<FabricUserInfo> users;
}
