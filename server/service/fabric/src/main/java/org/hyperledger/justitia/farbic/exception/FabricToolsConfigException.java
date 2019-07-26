package org.hyperledger.justitia.farbic.exception;

public class FabricToolsConfigException extends FabricToolsException {
    public FabricToolsConfigException() {
    }

    public FabricToolsConfigException(String message) {
        super(message);
    }

    public FabricToolsConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public FabricToolsConfigException(Throwable cause) {
        super(cause);
    }

    public FabricToolsConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
