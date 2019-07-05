package org.hyperledger.justitia.identity.service.beans;

import lombok.Data;
import org.hyperledger.justitia.identity.service.beans.crypto.UserCrypto;

@Data
public class FabricUserInfo {
    private String id;
    private Boolean admin;
    private String tag;
    private UserCrypto crypto;
}
