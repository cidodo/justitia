package org.hyperledger.justitia.identity.service.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hyperledger.justitia.identity.service.beans.crypto.NodeCrypto;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrdererInfo extends NodeInfo {
    private String systemChainId;
}
