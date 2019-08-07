package org.hyperledger.justitia.channel.service.member;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hyperledger.justitia.channel.exception.ChannelServiceException;
import org.hyperledger.justitia.common.face.service.chaincode.ChaincodeRequestService;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeInvokeResult;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeQueryResult;
import org.hyperledger.justitia.common.bean.chaincode.TransactionRequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class MemberManageChaincode {
    private final ChaincodeRequestService chaincodeRequester;

    @Value("${fabric.channel.manage.chaincode}")
    private String memberManageChaincodeName = "CMSCC";

    @Autowired
    public MemberManageChaincode(ChaincodeRequestService chaincodeRequestService) {
        this.chaincodeRequester = chaincodeRequestService;
    }

    public enum RequestState {
        INVALID("invalid"),
        END("end"),
        SIGNING("signing"),
        ALL("");

        private String state;

        RequestState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }
    }

    public enum RequestType {
        ADD_MEMBER("addMember"),
        DELETE_MEMBER("deleteMember"),
        UPDATE_MEMBER("updateMember");

        private String type;

        RequestType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    /**
     * Initiate a proposal to change the channel configuration.
     */
    public String signRequests(CMSCCRequestBean requestInfo) {
        String channelId = requestInfo.getChannelId();
        ArrayList<String> args = requestInfo.getSignRequestsArgs();
        invokeChaincode(channelId, "signRequests", args);
        return requestInfo.getRequestId();
    }

    /**
     * Signature proposal initiated by other members in the response channel.
     */
    public void signResponses(CMSCCRequestBean responsesInfo) {
        String channelId = responsesInfo.getChannelId();
        ArrayList<String> args = responsesInfo.getSignResponsesArgs();
        invokeChaincode(channelId, "signResponses", args);
    }

    /**
     * Change the status of your own signed proposal。
     */
    public void updateRequestState(CMSCCRequestBean updateStateInfo) {
        String channelId = updateStateInfo.getChannelId();
        ArrayList<String> args = updateStateInfo.getUpdateRequestStateArgs();
        invokeChaincode(channelId, "updateRequestState", args);
    }

    /**
     * Query your own signature proposal.
     */
    public List<SignRequest> getMyRequests(String channelId, String state) {
        ArrayList<String> args = new ArrayList<>();
        args.add(state);
        return queryChaincode(channelId, "getMySignRequests", args, SignRequest.class);
    }

    /**
     * Query all signature responses for signature requests initiated by yourself
     */
    public List<SignResponse> getAllSignResponses(String channelId, String requestId) {
        ArrayList<String> args = new ArrayList<>();
        args.add(requestId);
        return queryChaincode(channelId, "getAllSignResponsesById", args, SignResponse.class);
    }

    /**
     * Query all requests currently in the "signing" state.
     */
    @Deprecated
    public List<SignRequest> getAllSigningRequest(String channelId) {
        return queryChaincode(channelId, "getAllSigningRequest", null, SignRequest.class);
    }

    /**
     * Query all requests that you have signed.
     */
    public List<SignResponse> getAllSignResponsesByChannel(String channelId) {
        return queryChaincode(channelId, "getMyAllSignResponses", null, SignResponse.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T queryChaincode(String channelId, String function, ArrayList<String> args, Class responseType) {
        TransactionRequestBean transactionRequestBean = new TransactionRequestBean();
        transactionRequestBean.setChannelName(channelId);
        transactionRequestBean.setFunction(function);
        transactionRequestBean.setArgs(args);
        transactionRequestBean.setChaincodeName(memberManageChaincodeName);
        ChaincodeQueryResult result = chaincodeRequester.query(transactionRequestBean);
        String payloadAsString = result.getPayloadAsString();
        if (result.isSuccess() && null != payloadAsString) {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, responseType);
            try {
                return (T) objectMapper.readValue(payloadAsString, javaType);
            } catch (IOException e) {
                throw new ChannelServiceException(ChannelServiceException.CMSCC_QUERY_RESULT_DATA_ERROR, function, memberManageChaincodeName);
            }
        } else {
            throw new ChannelServiceException(ChannelServiceException.CMSCC_QUERY_ERROR, function, memberManageChaincodeName);
        }
    }

    private void invokeChaincode(String channelId, String function, ArrayList<String> args) {
        TransactionRequestBean transactionRequestBean = new TransactionRequestBean();
        transactionRequestBean.setChannelName(channelId);
        transactionRequestBean.setFunction(function);
        transactionRequestBean.setArgs(args);
        transactionRequestBean.setChaincodeName(memberManageChaincodeName);
        ChaincodeInvokeResult result = chaincodeRequester.invoke(transactionRequestBean);
        if (!result.isSuccess()) {
            throw new ChannelServiceException(ChannelServiceException.CMSCC_INVOKE_ERROR, function, memberManageChaincodeName);
        }
    }


    @Data
    public static class SignRequest {
        private String id;
        private String from;
        private String content;
        private String[] msp;
        private String desc;
        private long version;
        private String status;
        private long time;
        private String type;
    }

    @Data
    public static class SignResponse {
        private String id;
        private String reject;
        private String from;
        private String to;
        private String signature;
        private String reason;
        private long time;
    }

}
