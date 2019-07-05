package org.hyperledger.justitia.scheduler.controller.identity.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetPeerBean extends SetNodeBean{
    private Boolean couchdbEnable;
//    private SetNodeBean couchdb;
}
