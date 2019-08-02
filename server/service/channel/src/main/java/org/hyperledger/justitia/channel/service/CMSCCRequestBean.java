package org.hyperledger.justitia.channel.service;

import lombok.Data;
import org.hyperledger.justitia.channel.service.member.MemberManageChaincode;
import org.hyperledger.justitia.common.utils.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Set;

@Data
public class CMSCCRequestBean {
    //common
    private String channelId;
    private String requestId;

    //proposal
    private byte[] updatedConfigTrans;
    private MemberManageChaincode.RequestType requestType;
    private long sequence;
    private String description;
    private Set<String> membersMspId;

    //response
    private String requester;
    private boolean reject;
    private byte[] signature;
    private String reason;

    public CMSCCRequestBean(String channelId) {
        this.channelId = channelId;
    }

    //update proposal
    private MemberManageChaincode.RequestState state;

    private String generateRequestId() {
        int random = (int) (Math.random() * 1000);
        String requestId = channelId + "|" + sequence + "|" + System.currentTimeMillis() + "|" + random;
        return DatatypeConverter.printHexBinary(requestId.getBytes()).toLowerCase();
    }

    public ArrayList<String> getSignRequestsArgs() {
        ArrayList<String> args = new ArrayList<>();
        args.add(generateRequestId());
        args.add(DatatypeConverter.printHexBinary(updatedConfigTrans));
        args.add(requestType.getType());
        args.add(Long.toString(sequence));
        args.add(description);
        args.add(StringUtils.join(membersMspId, ","));
        return args;
    }

    public ArrayList<String> getSignResponsesArgs() {
        ArrayList<String> args = new ArrayList<>();
        args.add(requestId);
        args.add(requester);
        if (reject) {
            args.add("Y");
        } else {
            args.add("N");
        }
        args.add(Base64.getEncoder().encodeToString(signature));
        args.add(reason);
        return args;
    }

    public ArrayList<String> getUpdateRequestStateArgs() {
        ArrayList<String> args = new ArrayList<>();
        args.add(requestId);
        args.add(state.getState());
        return args;
    }
}
