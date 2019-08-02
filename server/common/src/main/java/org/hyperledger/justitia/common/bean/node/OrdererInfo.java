package org.hyperledger.justitia.common.bean.node;

import lombok.Data;
import org.hyperledger.justitia.common.bean.node.Node;

@Data
public class OrdererInfo extends Node {
    private String systemChainId;

    @Override
    public NodeType getNodeType() {
        return NodeType.ORDERER;
    }

    @Override
    protected String generateMspId() {
        return getId() + "-orderer-msp";
    }

    @Override
    protected String generateCaId() {
        return getId() + "-orderer-ca";
    }

    @Override
    protected String generateTlsCaId() {
        return getId() + "-orderer-tls-ca";
    }
}