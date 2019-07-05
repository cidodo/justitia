package org.hyperledger.justitia.farbic.exception;

public class HFClientContextException extends Exception {
    public HFClientContextException() {
    }

    public HFClientContextException(String message) {
        super(message);
    }

    public HFClientContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public HFClientContextException(Throwable cause) {
        super(cause);
    }

    public HFClientContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
