package org.hyperledger.justitia.channel.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class ChannelManageException extends ServiceException {
    public ChannelManageException() {
    }

    public ChannelManageException(String message) {
        super(message);
    }

    public ChannelManageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelManageException(Throwable cause) {
        super(cause);
    }

    public ChannelManageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
