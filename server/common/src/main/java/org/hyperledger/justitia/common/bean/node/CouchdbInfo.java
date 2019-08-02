package org.hyperledger.justitia.common.bean.node;

import lombok.Data;
import org.hyperledger.justitia.common.bean.node.Node;


@Data
public class CouchdbInfo extends Node {

    @Override
    public NodeType getNodeType() {
        return NodeType.COUCHDB;
    }

    @Override
    protected String generateMspId() {
        return getId() + "-couchdb-msp";
    }

    @Override
    protected String generateCaId() {
        return getId() + "-couchdb-ca";
    }

    @Override
    protected String generateTlsCaId() {
        return getId() + "-couchdb-tls-ca";
    }
}