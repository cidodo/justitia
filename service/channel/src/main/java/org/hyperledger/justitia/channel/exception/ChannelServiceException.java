package org.hyperledger.justitia.channel.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class ChannelServiceException extends ServiceException {
    public ChannelServiceException(int errorCode) {
        super(errorCode);
    }

    public ChannelServiceException(int errorCode, String message) {
        super(errorCode, message);
    }

    public ChannelServiceException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ChannelServiceException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ChannelServiceException(int errorCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    protected int getErrorType() {
        return ServiceException.CHANNEL_SERVICE_ERROR;
    }
}
