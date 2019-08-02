package org.hyperledger.justitia.channel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.justitia.channel.dao.ChannelConfigTaskDao;
import org.hyperledger.justitia.channel.service.member.MemberManageChaincode;
import org.hyperledger.justitia.channel.service.member.MemberManageChaincode.RequestType;
import org.hyperledger.justitia.common.bean.channel.ChannelConfigTask;
import org.hyperledger.justitia.common.face.service.channel.ChannelTaskService;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.hyperledger.justitia.common.face.service.fabric.FabricToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class ChannelMemberService extends ChannelConfigService implements ChannelTaskService {
    private final MemberManageChaincode manageChaincode;
    private final ChannelConfigTaskDao channelConfigTaskDao;

    @Autowired
    public ChannelMemberService(ChannelService channelService, FabricToolsService fabricToolsService,
                                MemberManageChaincode manageChaincode, ChannelConfigTaskDao channelConfigTaskDao) {
        super(channelService, fabricToolsService);
        this.manageChaincode = manageChaincode;
        this.channelConfigTaskDao = channelConfigTaskDao;
    }

    public void addMember(String channelId, String memberName, String config, String description) {
        JsonNode memberConfig = new ObjectMapper().readTree(config);

        byte[] original = getChainConfigBytes(channelId);
        JsonNode configJson = decodeChainConfig(original);
        addChainConfig(configJson, "", memberName, memberConfig);
        byte[] updated = encodeChainConfig(configJson);
        byte[] updateConfigTrans = computeUpdateChainConfig(original, updated, channelId);

        String proposalId = createSignProposal(channelId, original, updateConfigTrans, description, RequestType.ADD_MEMBER);

        //todo DB
    }

    public void deleteMember(String channelId, String memberName, String description) {
        byte[] original = getChainConfigBytes(channelId);
        JsonNode configJson = getChainConfigAsJson(original);
        deleteChainConfig(configJson, "");
        byte[] updated = encodeChainConfig(configJson);
        byte[] updateConfigTrans = computeUpdateChainConfig(original, updated, channelId);

        String proposalId = createSignProposal(channelId, original, updateConfigTrans, description, RequestType.ADD_MEMBER);
    }

    public void updateMember(String channelId, String memberName, String config, String description) {
        JsonNode memberConfig = new ObjectMapper().readTree(config);

        byte[] original = getChainConfigBytes(channelId);
        JsonNode configJson = decodeChainConfig(original);
        updateChainConfig(configJson, "", memberName, memberConfig);
        byte[] updated = encodeChainConfig(configJson);
        byte[] updateConfigTrans = computeUpdateChainConfig(original, updated, channelId);

        String proposalId = createSignProposal(channelId, original, updateConfigTrans, description, RequestType.ADD_MEMBER);
    }

    private String createSignProposal(String channelId, byte[] original, byte[] updatedConfigTrans, String description,
                                      RequestType requestType) {
        Configtx.Config originalConfig = parseChainConfig(original);
        long sequence = getConfigSequence(originalConfig);
        Set<String> membersMspId = getMembersMspId(originalConfig);

        CMSCCRequestBean proposalInfo = new CMSCCRequestBean(channelId);
        proposalInfo.setUpdatedConfigTrans(updatedConfigTrans);
        proposalInfo.setRequestType(requestType);
        proposalInfo.setSequence(sequence);
        proposalInfo.setDescription(description);
        proposalInfo.setMembersMspId(membersMspId);
        return manageChaincode.signRequests(proposalInfo);
    }

    public void createSignProposalProposal(String channelId, String proposalId, boolean reject, String reason) {
        ChannelConfigTask task = channelConfigTaskDao.getTask(proposalId);
        CMSCCRequestBean proposalInfo = new CMSCCRequestBean(channelId);
        proposalInfo.setRequestId(proposalId);
        proposalInfo.setRequester(task.getRequester());
        if (reject) {
            proposalInfo.setReason(reason);
        } else {
            byte[] sign = signUpdateConfigTrans(task.getContent());
            proposalInfo.setSignature(sign);
        }
        manageChaincode.signResponses(proposalInfo);
    }

    public void submitProposal(String proposalId) {
        ChannelConfigTask task = getTask(proposalId, MemberManageChaincode.RequestState.SIGNING);
        String channelId = task.getChannelId();
        byte[] updateConfigTrans = task.getContent();

        List<MemberManageChaincode.SignResponse> allSignResponses = manageChaincode.getAllSignResponses(channelId, proposalId);
        ArrayList<byte[]> signatures = new ArrayList<>();
        if (null != allSignResponses) {
            for (MemberManageChaincode.SignResponse signResponse : allSignResponses) {
                if (null != signResponse.getSignature()) {
                    byte[] signBytes = DatatypeConverter.parseHexBinary(signResponse.getSignature());
                    signatures.add(signBytes);
                }
            }
        }

        submitConfigUpdate(channelId, updateConfigTrans, signatures);
    }

    public void recallProposal(String proposalId) {
        ChannelConfigTask task = getTask(proposalId, MemberManageChaincode.RequestState.SIGNING);
        try {
            CMSCCRequestBean updateStateInfo = new CMSCCRequestBean(task.getChannelId());
            updateStateInfo.setRequestId(task.getProposalId());
            updateStateInfo.setState(MemberManageChaincode.RequestState.INVALID);
            manageChaincode.updateRequestState(updateStateInfo);
            channelConfigTaskDao.updateTaskStatus(task.getProposalId(), "invalid");
        } catch (MemberManageException e) {
            throw new (String.format("Task %s recall failed.", proposalId), e);
        }
    }



    @Override
    public List<TaskInfo> getTasks() {
        return null;
    }

    @Override
    public TaskInfo getTask(String proposalId) {
        return channelConfigTaskDao.getTask(proposalId);
    }

    private ChannelConfigTask getTask(String requestId, MemberManageChaincode.RequestState expectedState) {
        ChannelConfigTask task = channelConfigTaskDao.getTask(requestId);
        if (!expectedState.getState().equals(task.getStatus())) {
            throw new
        }
        return task;
    }

    @Override
    public void channelConfigTaskResponse(String requestId, boolean reject, String reason) {

    }

    @Override
    public void submitRequest(String requestId) {

    }

    @Override
    public void recallMyRequest(String requestId) {

    }

    @Override
    public void deleteTask(String requestId) {

    }
}
