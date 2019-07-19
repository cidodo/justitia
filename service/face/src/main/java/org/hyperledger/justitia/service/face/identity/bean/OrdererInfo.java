package org.hyperledger.justitia.service.face.identity.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrdererInfo extends NodeInfo {
    private String systemChainId;
}
