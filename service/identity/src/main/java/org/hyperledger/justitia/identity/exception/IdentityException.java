package org.hyperledger.justitia.identity.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class IdentityException extends ServiceException {
    public IdentityException() {
    }

    public IdentityException(String message) {
        super(message);
    }

    public IdentityException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentityException(Throwable cause) {
        super(cause);
    }

    public IdentityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
