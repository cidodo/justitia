package org.hyperledger.justitia.chaincode.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class ChaincodeServiceException extends ServiceException {
    public ChaincodeServiceException(int errorCode) {
        super(errorCode);
    }

    public ChaincodeServiceException(int errorCode, Object... args) {
        super(errorCode, args);
    }

    public ChaincodeServiceException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ChaincodeServiceException(int errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }

    @Override
    protected int getErrorType() {
        return ServiceException.CHAINCODE_SERVICE_ERROR;
    }
}
