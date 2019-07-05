package org.hyperledger.justitia.identity.service.beans.crypto;

import lombok.Data;

@Data
public class UserCrypto {
    private MspInfo mspInfo;
    private TlsInfo tlsInfo;
}
