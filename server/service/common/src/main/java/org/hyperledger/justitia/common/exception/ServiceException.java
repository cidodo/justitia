package org.hyperledger.justitia.common.exception;

import org.hyperledger.justitia.common.utils.I18nUtils;

import java.util.Locale;

public abstract class ServiceException extends RuntimeException {
    /**
     * errorCode是一个长度为六位的整数，形如AABBCC。
     * AA:异常类型
     * BB:异常类别
     * CC:异常编号
     */
    private int errorCode;
    public static final int CHAINCODE_SERVICE_ERROR    = 11;
    public static final int CHANNEL_SERVICE_ERROR      = 12;
    public static final int COMMON_SERVICE_ERROR       = 13;
    public static final int FABRIC_SERVICE_ERROR       = 14;
    public static final int FACE_SERVICE_ERROR         = 15;
    public static final int IDENTITY_SERVICE_ERROR     = 16;
    public static final int NODE_SERVICE_ERROR         = 17;

    private Object [] args;

    public ServiceException(int errorCode) {
        this.errorCode = errorCode;
        this.args = null;
    }

    public ServiceException(int errorCode, Object... args) {
        this.errorCode = errorCode;
        this.args = args;
    }

    public ServiceException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public ServiceException(int errorCode, Throwable cause, Object... args) {
        super(cause);
        this.errorCode = errorCode;
        this.args =args;
    }

    public String getLocalizedMessage(Locale locale) {
        return I18nUtils.getMessage(Integer.toString(errorCode), args, locale);
    }

    abstract protected int getErrorType();

    @Override
    public String getMessage() {
        String message = "[" + getErrorType() + "]" + "[" + errorCode + "]"
                + I18nUtils.getMessage(Integer.toString(errorCode), args, Locale.ENGLISH);
        return message + super.getMessage();
    }
}
