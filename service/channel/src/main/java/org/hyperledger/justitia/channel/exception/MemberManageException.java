package org.hyperledger.justitia.channel.exception;


import org.hyperledger.justitia.common.exception.ServiceException;

public class MemberManageException extends ServiceException {
    public MemberManageException() {
    }

    public MemberManageException(String message) {
        super(message);
    }

    public MemberManageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberManageException(Throwable cause) {
        super(cause);
    }

    public MemberManageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
