package org.hyperledger.justitia.common.face.modules.identity.beans.crypto;

import lombok.Data;

@Data
public class CaInfo {
    private String cert;
    private String key;
}
