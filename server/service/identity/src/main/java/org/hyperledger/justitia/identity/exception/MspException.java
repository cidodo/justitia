package org.hyperledger.justitia.identity.exception;

public class MspException extends Exception {
    public MspException() {
    }

    public MspException(String message) {
        super(message);
    }

    public MspException(String message, Throwable cause) {
        super(message, cause);
    }

    public MspException(Throwable cause) {
        super(cause);
    }

    public MspException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
