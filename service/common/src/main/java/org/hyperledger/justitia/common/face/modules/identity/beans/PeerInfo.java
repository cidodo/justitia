package org.hyperledger.justitia.common.face.modules.identity.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PeerInfo extends NodeInfo{
    //couchdb
    private Boolean couchdbEnable;
    private CouchdbInfo couchdb;
}
