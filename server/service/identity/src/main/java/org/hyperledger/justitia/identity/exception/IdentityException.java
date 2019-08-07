package org.hyperledger.justitia.identity.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class IdentityException extends ServiceException {
    /**
     * Identity does not exist: 01
     */
    public static final int ORGANIZATION_DOES_NOT_EXITS = 160111;
    public static final int NO_ORGANIZATION             = 160112;
    public static final int USER_DOES_NOT_EXITS         = 160121;
    public static final int NOT_FABRIC_USER             = 100122;
    public static final int PEER_DOES_NOT_EXITS         = 160131;
    public static final int NO_PEER                     = 160132;
    public static final int ORDERER_DOES_NOT_EXITS      = 160141;
    public static final int NO_ORDERER                  = 160142;

    /**
     * Incomplete configuration: 02
     */
    public static final int INCOMPLETE_CA               = 160201;
    public static final int INCOMPLETE_TLSCA            = 160202;

    /**
     * MSP: 03
     */
    public static final int GENERATE_ORGANIZATION_MSP_ERROR   = 160311;
    public static final int GENERATE_NODE_MSP_ERROR           = 160321;
    public static final int GENERATE_USER_MSP_ERROR           = 160331;



    public IdentityException(int errorCode) {
        super(errorCode);
    }

    public IdentityException(int errorCode, Object... args) {
        super(errorCode, args);
    }

    public IdentityException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public IdentityException(int errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }

    @Override
    protected int getErrorType() {
        return ServiceException.IDENTITY_SERVICE_ERROR;
    }
}
