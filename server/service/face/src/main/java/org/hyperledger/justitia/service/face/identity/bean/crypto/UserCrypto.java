package org.hyperledger.justitia.service.face.identity.bean.crypto;

import lombok.Data;

@Data
public class UserCrypto {
    private MspInfo mspInfo;
    private TlsInfo tlsInfo;
}
