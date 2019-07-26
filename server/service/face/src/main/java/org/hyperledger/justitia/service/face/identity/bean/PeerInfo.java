package org.hyperledger.justitia.service.face.identity.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

@Data
@EqualsAndHashCode(callSuper = false)
public class PeerInfo extends NodeInfo{
    //couchdb
    private Boolean couchdbEnable;
    private CouchdbInfo couchdb;
}
