package org.hyperledger.justitia.identity.service.beans.crypto;

import lombok.Data;

@Data
public class CaInfo {
    private String cert;
    private String key;
}
