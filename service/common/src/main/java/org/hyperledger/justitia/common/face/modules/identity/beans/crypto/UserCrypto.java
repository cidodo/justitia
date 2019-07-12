package org.hyperledger.justitia.common.face.modules.identity.beans.crypto;

import lombok.Data;

@Data
public class UserCrypto {
    private MspInfo mspInfo;
    private TlsInfo tlsInfo;
}
