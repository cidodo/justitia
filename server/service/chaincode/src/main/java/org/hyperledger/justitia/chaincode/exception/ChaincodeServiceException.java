package org.hyperledger.justitia.chaincode.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class ChaincodeServiceException extends ServiceException {
    /**
     * Chaincode manage: 01
     */
    public static final int CHAINCODE_INSTALL_ERROR         = 110111;
    public static final int CHAINCODE_INSTALL_FAILED        = 110112;
    public static final int CHAINCODE_INSTANTIATE_ERROR     = 110121;
    public static final int CHAINCODE_UPGRADE_ERROR         = 110131;

    /**
     * Chaincode request: 02
     */
    public static final int ENDORSER_FAILED             = 110201;
    public static final int CHAINCODE_RESULT_DATA_ERROR = 110202;
    public static final int CHAINCODE_QUERY_ERROR       = 110211;
    public static final int CHAINCODE_INVOKE_ERROR      = 110212;
    public static final int CHAINCODE_ENDORSEMENT_ERROR = 110213;

    /**
     * Query chaincode information: 03
     */
    public static final int QUERY_INSTALLED_CHAINCODE_ERROR     = 110301;
    public static final int QUERY_INSTANTIATED_CHAINCODE_ERROR  = 110302;

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
