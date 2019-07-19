package org.hyperledger.justitia.farbic.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class FabricServiceException extends ServiceException {
    /**
     * 配置不存在:01
     */
    //Fabric user
    public static final int NOT_FOUND_USER_BY_ID     = 140111;
    public static final int NO_USERS                 = 140112;
    public static final int NO_ADMIN_USER            = 140113;
    //Peer
    public static final int NOT_FOUND_PEER_BY_ID     = 140121;
    public static final int NO_PEERS                 = 140122;
    public static final int NOT_FOUND_PEER_BY_CHANNEL = 140123;
    //Orderer
    public static final int NOT_FOUND_ORDERER_BY_ID  = 140131;
    public static final int NO_ORDERERS              = 140132;

    /**
     * 对象创建失败:02
     */
    //HFClient
    public static final int CREATE_HFCLIENT_ERROR    = 140210;
    public static final int CRYPTO_SUITE_ERROR       = 140211;
    public static final int USER_CONTEXT_ERROR       = 140212;
    public static final int FABRIC_USER_CRYPTO_PARSE_ERROR = 140213;
    //Channel
    public static final int CREATE_CHANNEL_ERROR     = 140220;
    public static final int CHANNEL_INITIALIZE_ERROR = 140223;
    //Orderer
    public static final int CREATE_ORDERER_ERROR     = 140230;
    //Peer
    public static final int CREATE_PEER_ERROR        = 140240;

    /**
     * Fabric交易相关: 03
     */
    public static final int ENDORSER_FAILED          = 140321;
    public static final int SEND_TRANSACTION_FAILED  = 140322;
    public static final int WAIT_RESULT_EXCEPTION    = 140323;
    public static final int INVALID_TRANSACTION      = 140324;

    /**
     * 其他异常:99
     */
    public static final int UNKONWN_FABRIC_ERROR     = 149999;

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
