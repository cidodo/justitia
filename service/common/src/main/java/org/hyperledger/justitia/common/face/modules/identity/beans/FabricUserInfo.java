package org.hyperledger.justitia.common.face.modules.identity.beans;

import lombok.Data;
import org.hyperledger.justitia.common.face.modules.identity.beans.crypto.UserCrypto;

@Data
public class FabricUserInfo {
    private String id;
    private Boolean admin;
    private String tag;
    private UserCrypto crypto;
}
