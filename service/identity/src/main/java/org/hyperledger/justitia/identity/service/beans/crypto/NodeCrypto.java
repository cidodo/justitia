package org.hyperledger.justitia.identity.service.beans.crypto;

import lombok.Data;

@Data
public class NodeCrypto {
    private MspInfo mspInfo;
    private TlsInfo tlsInfo;
}
