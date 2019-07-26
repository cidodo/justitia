package org.hyperledger.justitia.identity.exception;

import org.springframework.dao.DuplicateKeyException;

public class IdentityDuplicateKeyException extends DuplicateKeyException {

    public IdentityDuplicateKeyException(String msg) {
        super(msg);
    }

    public IdentityDuplicateKeyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
