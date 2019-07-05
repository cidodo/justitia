package org.hyperledger.justitia.channel.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class ConsortiumManageException extends ServiceException {
    public ConsortiumManageException() {
    }

    public ConsortiumManageException(String message) {
        super(message);
    }

    public ConsortiumManageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsortiumManageException(Throwable cause) {
        super(cause);
    }

    public ConsortiumManageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
