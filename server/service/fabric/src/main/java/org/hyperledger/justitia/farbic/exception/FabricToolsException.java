package org.hyperledger.justitia.farbic.exception;

public class FabricToolsException extends Exception {
    public FabricToolsException() {
    }

    public FabricToolsException(String message) {
        super(message);
    }

    public FabricToolsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FabricToolsException(Throwable cause) {
        super(cause);
    }

    public FabricToolsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
