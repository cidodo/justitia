package org.hyperledger.justitia.service.face.identity.bean.crypto;

import lombok.Data;

@Data
public class TlsInfo {
    private String ca;
    private String cert;
    private String key;
}
