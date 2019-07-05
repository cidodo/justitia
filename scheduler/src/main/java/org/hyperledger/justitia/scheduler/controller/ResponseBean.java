package org.hyperledger.justitia.scheduler.controller;

import lombok.Getter;

import java.io.Serializable;

public class ResponseBean<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int UNKNOWN_EXCEPTION = -99;

    public static final int CONTEXT_NOT_INIT = -2;
    public static final int NO_LOGIN = -1;
    private static final int SUCCESS = 0;
    public static final int CHECK_FAIL = 1;
    public static final int NO_PERMISSION = 2;
    public static final int PULOAD_FILE_FAIL = 3;

    //identity
    public static final int IDENTITY_DUPLICATE_KEY = 1001;


    @Getter private T data;
    @Getter private int code = SUCCESS;
    @Getter private String msg = "success";

    public ResponseBean success() {
        return this;
    }

    public ResponseBean<T> success(T data) {
        this.data = data;
        return this;
    }

    public ResponseBean failure(int code, String message) {
        this.msg = message;
        this.code = code;
        return this;
    }

    public ResponseBean failure(Throwable e) {
        this.msg = e.toString();
        this.code = UNKNOWN_EXCEPTION;
        return this;
    }
}
