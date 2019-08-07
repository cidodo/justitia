package org.hyperledger.justitia.scheduler.controller.aspect;

import org.hyperledger.justitia.common.exception.ServiceException;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.exception.UploadFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * 全局的异常切面，用于处理Controller请求异常
 */
@ControllerAdvice
@ResponseBody
public class ControllerExceptionAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionAspect.class);

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseBean handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        LOGGER.debug("Could not read json.", e);
        return new ResponseBean().failure(HttpStatus.BAD_REQUEST.value(), "Could not read json");
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseBean handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.debug("Parameter validation exception.", e);
        return new ResponseBean().failure(HttpStatus.BAD_REQUEST.value(), "Parameter validation exception.");
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class})
    public ResponseBean handleBindException(BindException e) {
        LOGGER.debug("Parameter bind exception.", e);
        return new ResponseBean().failure(HttpStatus.BAD_REQUEST.value(), "Parameter bind exception.");
    }

    /**
     * 405 - Method not allowed
     * 是ServletException的子类，需要Servlet API支持
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseBean handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        LOGGER.warn("Request method not supported.", e);
        return new ResponseBean().failure(HttpStatus.METHOD_NOT_ALLOWED.value(), "Request method not supported.");
    }

    /**
     * 415 - Unsupported media type
     * 是ServletException的子类，需要Servlet API支持
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResponseBean handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        LOGGER.warn("Content type not supported.", e);
        return new ResponseBean().failure(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Content type not supported");
    }

    /**
     * 500 - Internal server error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseBean handleException(Exception e) {
        LOGGER.error("Internal server error.", e);
        return new ResponseBean().failure(e);
    }

    /**
     * 500 - Internal server error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseBean handleRuntimeException(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return new ResponseBean().failure(e);
    }



    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServiceException.class)
    public ResponseBean handleException(ServiceException e) {
        LOGGER.error("Service request failed.", e);
        StringBuilder msg = new StringBuilder(e.getMessage());
        Throwable cause = e.getCause();
        if (cause != null) {
            msg.append(" ").append(cause.getMessage());
        }
        return new ResponseBean().failure(ResponseBean.IDENTITY_DUPLICATE_KEY, msg.toString());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(UploadFileException.class)
    public ResponseBean handleException(UploadFileException e) {
        LOGGER.error("Upload file read failed.", e);
        return new ResponseBean().failure(ResponseBean.PULOAD_FILE_FAIL, e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseBean handleException(DuplicateKeyException e) {
        LOGGER.error("Duplicate key exception.", e);
        return new ResponseBean().failure(ResponseBean.IDENTITY_DUPLICATE_KEY, e.getMessage());
    }
}
