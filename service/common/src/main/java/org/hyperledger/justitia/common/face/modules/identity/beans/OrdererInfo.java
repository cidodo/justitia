package org.hyperledger.justitia.common.face.modules.identity.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrdererInfo extends NodeInfo {
    private String systemChainId;
}
