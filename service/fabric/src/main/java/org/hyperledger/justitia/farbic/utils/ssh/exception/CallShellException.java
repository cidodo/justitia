package org.hyperledger.justitia.farbic.utils.ssh.exception;

public class CallShellException extends RuntimeException {
    public CallShellException() {
    }

    public CallShellException(String message) {
        super(message);
    }

    public CallShellException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallShellException(Throwable cause) {
        super(cause);
    }

    public CallShellException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
