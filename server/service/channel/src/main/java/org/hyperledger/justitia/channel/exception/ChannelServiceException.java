package org.hyperledger.justitia.channel.exception;

import org.hyperledger.justitia.common.exception.ServiceException;

public class ChannelServiceException extends ServiceException {
    /**
     * Channel configuration exception: 01
     */
    public static final int GET_CHAIN_CONFIG_ERROR      = 120101;
    public static final int DECODE_CHAIN_CONFIG_ERROR   = 120111;
    public static final int ENCODE_CHAIN_CONFIG_ERROR   = 120112;
    public static final int PARSE_CHAIN_CONFIG_ERROR    = 120113;
    public static final int COMPUTE_UPDATE_ERROR        = 120114;
    public static final int CHAIN_CONFIG_DATA_ERROR     = 120120;
    public static final int NOT_EXIST_PATH_IN_CONFIG    = 120121;
    public static final int CHAIN_MEMBER_EXIST          = 120122;
    public static final int CHAIN_MEMBER_NOT_EXIST      = 120123;

    /**
     * Channel update transaction exception: 02
     */
    public static final int UPDATE_CHAIN_TRANSACTION_SIGN_ERROR = 120201;
    public static final int UPDATE_TRANSACTION_SUBMIT_ERROR     = 120202;
    public static final int CREATE_CHAIN_TRANSACTION_SIGN_ERROR = 120211;
    public static final int CREATE_TRANSACTION_SUBMIT_ERROR     = 120212;

    /**
     * CMSCC ERROR: 03
     */
    public static final int CMSCC_QUERY_ERROR               = 120301;
    public static final int CMSCC_INVOKE_ERROR              = 120302;
    public static final int CMSCC_QUERY_RESULT_DATA_ERROR   = 120311;
    public static final int CMSCC_INVOKE_RESULT_DATA_ERROR  = 120312;

    /**
     * System chain manage: 04
     */
    public static final int ORGANIZATION_TYPE_ERROR         = 120401;
    public static final int ORDERER_DOES_NOT_EXITS          = 120402;

    /**
     * Channel configuration proposal manage exception: 05
     */
    public static final int PROPOSAL_DOES_NOT_EXITS     = 120501;
    public static final int PROPOSAL_CANNOT_BE_SUBMITTED = 120502;
    public static final int PROPOSAL_CANNOT_BE_RECALL   = 120503;
    public static final int PROPOSAL_RECALL_ERROR       = 120511;

    /**
     * Peer join channel exception: 11
     */
    public static final int PEER_JOIN_ERROR         = 121101;

    /**
     * Unknown exception: 99
     */
    public static final int UNKNOWN_EXCEPTION           = 129999;

    public ChannelServiceException(int errorCode) {
        super(errorCode);
    }

    public ChannelServiceException(int errorCode, Object... args) {
        super(errorCode, args);
    }

    public ChannelServiceException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ChannelServiceException(int errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }

    /**
     * Channel configuration proposal exception: 02
     */



    @Override
    protected int getErrorType() {
        return ServiceException.CHANNEL_SERVICE_ERROR;
    }
}
