package org.hyperledger.justitia.channel.service.member;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hyperledger.justitia.channel.service.CMSCCRequestBean;
import org.hyperledger.justitia.common.face.service.chaincode.ChaincodeRequestService;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeInvokeResult;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeQueryResult;
import org.hyperledger.justitia.common.bean.chaincode.TransactionRequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MemberManageChaincode {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberManageChaincode.class);
    private final ChaincodeRequestService chaincodeRequester;

    @Value("${fabric.channel.manage.chaincode}")
    private String memberManageChaincodeName;


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
        MODIFY_ORG_CONFIG("modifyOrgConfig");

        private String type;

        RequestType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    /**
     * 发起更改通道配置的请求
     */
    public String signRequests(CMSCCRequestBean requestInfo) {
        String channelId = requestInfo.getChannelId();
        ArrayList<String> args = requestInfo.getSignRequestsArgs();
        invokeChaincode(channelId, "signRequests", args);
        return requestInfo.getRequestId();
    }

    /**
     * 应答通道内其他成员发起的签名请求
     */
    public void signResponses(CMSCCRequestBean responsesInfo) {
        String channelId = responsesInfo.getChannelId();
        ArrayList<String> args = responsesInfo.getSignResponsesArgs();
        invokeChaincode(channelId, "signResponses", args);
    }

    /**
     * 更改自己发起的签名申请的状态
     */
    public void updateRequestState(CMSCCRequestBean updateStateInfo) {
        String channelId = updateStateInfo.getChannelId();
        ArrayList<String> args = updateStateInfo.getUpdateRequestStateArgs();
        invokeChaincode(channelId, "updateRequestState", args);
    }

    /**
     * 查询自己发起的签名申请
     *
     * @param channelId 通道ID
     * @param state     请求状态，""表示全匹配
     */
    public List<SignRequest> getMyRequests(String channelId, String state) {
        ArrayList<String> args = new ArrayList<>();
        args.add(state);
        return queryChaincode(channelId, "getMySignRequests", args, SignRequest.class);
    }

    /**
     * 查询自己发起的签名申请的所有签名应答
     *
     * @param channelId 通道ID
     * @param requestId 请求ID
     */
    public List<SignResponse> getAllSignResponses(String channelId, String requestId) {
        ArrayList<String> args = new ArrayList<>();
        args.add(requestId);
        return queryChaincode(channelId, "getAllSignResponsesById", args, SignResponse.class);
    }

    /**
     * 查询当前处于signing状态的全部请求
     *
     * @param channelId 通道ID
     */
    @Deprecated
    public List<SignRequest> getAllSigningRequest(String channelId) {
        return queryChaincode(channelId, "getAllSigningRequest", null, SignRequest.class);
    }

    /**
     * 查询自己签名过的全部请求
     *
     * @param channelId 通道ID
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
            return (T) objectMapper.readValue(payloadAsString, javaType);
        } else {
            throw new MemberManageException(String.format("Chaincode function %s of %s call failed.", function, memberManageChaincodeName));
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
            throw new MemberManageException
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
