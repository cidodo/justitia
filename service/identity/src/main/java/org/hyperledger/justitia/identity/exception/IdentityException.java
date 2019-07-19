package org.hyperledger.justitia.identity.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class IdentityException extends ServiceException {


    public IdentityException(int errorCode) {
        super(errorCode);
    }

    public IdentityException(int errorCode, Object... args) {
        super(errorCode, args);
    }

    public IdentityException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public IdentityException(int errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }

    @Override
    protected int getErrorType() {
        return ServiceException.IDENTITY_SERVICE_ERROR;
    }
}
