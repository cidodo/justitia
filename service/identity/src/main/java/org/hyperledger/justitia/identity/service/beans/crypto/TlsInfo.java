package org.hyperledger.justitia.identity.service.beans.crypto;

import lombok.Data;

@Data
public class TlsInfo {
    private String ca;
    private String cert;
    private String key;
}
