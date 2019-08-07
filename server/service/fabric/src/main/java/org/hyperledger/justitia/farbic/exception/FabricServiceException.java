package org.hyperledger.justitia.farbic.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class FabricServiceException extends ServiceException {
    /**
     * Configuration does not exist: 01
     */
    //Fabric user
    public static final int NOT_FOUND_USER_BY_ID     = 140111;
    public static final int NO_USERS                 = 140112;
    public static final int NO_ADMIN_USER            = 140113;
    //PeerInfo
    public static final int NOT_FOUND_PEER_BY_ID     = 140121;
    public static final int NO_PEERS                 = 140122;
    public static final int NOT_FOUND_PEER_BY_CHANNEL = 140123;
    //OrdererInfo
    public static final int NOT_FOUND_ORDERER_BY_ID  = 140131;
    public static final int NO_ORDERERS              = 140132;

    /**
     * Object creation failed: 02
     */
    //HFClient
    public static final int CREATE_HFCLIENT_ERROR    = 140210;
    public static final int CRYPTO_SUITE_ERROR       = 140211;
    public static final int USER_CONTEXT_ERROR       = 140212;
    public static final int FABRIC_USER_CRYPTO_PARSE_ERROR = 140213;
    //Channel
    public static final int CREATE_CHANNEL_ERROR     = 140220;
    public static final int CHANNEL_INITIALIZE_ERROR = 140223;
    //OrdererInfo
    public static final int CREATE_ORDERER_ERROR     = 140230;
    //PeerInfo
    public static final int CREATE_PEER_ERROR        = 140240;

    /**
     * Fabric transaction exception: 03
     */
    public static final int ENDORSER_FAILED          = 140321;
    public static final int SEND_TRANSACTION_FAILED  = 140322;
    public static final int WAIT_RESULT_EXCEPTION    = 140323;
    public static final int INVALID_TRANSACTION      = 140324;


    /**
     * Fabric tools exception: 04
     */
    public static final int CONFIGTXGEN_CALL_ERROR              = 140411;
    public static final int CONFIGTXGEN_GENERATE_CONFIG_ERROR   = 140412;
    public static final int CONFIGTXLATOR_CALL_ERROR            = 140421;
    public static final int CRYPTOGEN_CALL_ERROR                = 140431;
    public static final int CRYPTOGEN_GENERATE_CONFIG_ERROE     = 140432;

    /**
     * Unknown exception: 99
     */
    public static final int UNKNOWN_FABRIC_ERROR     = 149999;

    public FabricServiceException(int errorCode) {
        super(errorCode);
    }

    public FabricServiceException(int errorCode, Object... args) {
        super(errorCode, args);
    }

    public FabricServiceException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public FabricServiceException(int errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }

    @Override
    protected int getErrorType() {
        return ServiceException.FABRIC_SERVICE_ERROR;
    }
}
