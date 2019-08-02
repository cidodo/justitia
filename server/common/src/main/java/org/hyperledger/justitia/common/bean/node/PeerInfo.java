package org.hyperledger.justitia.common.bean.node;

import lombok.Data;

@Data
public class PeerInfo extends Node {
    private Boolean couchdbEnable;
    private CouchdbInfo couchdb;

    public void setCouchdb(CouchdbInfo couchdb) {
        couchdb.setId(getId() + "-couchdb");
        this.couchdb = couchdb;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.PEER;
    }

    @Override
    protected String generateMspId() {
        return getId() + "-peer-msp";
    }

    @Override
    protected String generateCaId() {
        return getId() + "-peer-ca";
    }

    @Override
    protected String generateTlsCaId() {
        return getId() + "-peer-tls-ca";
    }
}