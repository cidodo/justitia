package org.hyperledger.justitia.channel.service;

import org.hyperledger.justitia.channel.dao.ChannelConfigProposalDao;
import org.hyperledger.justitia.channel.exception.ChannelServiceException;
import org.hyperledger.justitia.channel.service.member.CMSCCRequestBean;
import org.hyperledger.justitia.channel.service.member.ChannelMemberService;
import org.hyperledger.justitia.channel.service.member.MemberManageChaincode;
import org.hyperledger.justitia.common.bean.channel.ChannelConfigProposal;
import org.hyperledger.justitia.common.face.service.channel.ChannelConfigProposalService;
import org.hyperledger.justitia.common.face.service.fabric.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChannelConfigProposalServiceImpl implements ChannelConfigProposalService {
    private final NetworkService networkService;
    private final MemberManageChaincode memberManageChaincode;
    private final ChannelConfigProposalDao channelConfigProposalDao;
    private final ChannelMemberService channelMemberService;


    @Autowired
    public ChannelConfigProposalServiceImpl(NetworkService networkService, MemberManageChaincode manageChaincode,
                                            ChannelConfigProposalDao channelConfigProposalDao, ChannelMemberService channelMemberService) {
        this.networkService = networkService;
        this.memberManageChaincode = manageChaincode;
        this.channelConfigProposalDao = channelConfigProposalDao;
        this.channelMemberService = channelMemberService;
    }

    @Override
    public List<ChannelConfigProposal> getProposals() {
        return channelConfigProposalDao.selectProposal();
    }

    @Override
    public List<ChannelConfigProposal> getProposals(String expectedState) {
        return channelConfigProposalDao.selectProposalByStatus(expectedState);
    }

    @Override
    public ChannelConfigProposal getProposal(String proposalId) {
        ChannelConfigProposal proposal = channelConfigProposalDao.getProposal(proposalId);
        if (null == proposal) {
            throw new ChannelServiceException(ChannelServiceException.PROPOSAL_DOES_NOT_EXITS, proposalId);
        }
        return proposal;
    }

    String createSignProposal(CMSCCRequestBean proposalInfo) {
        return memberManageChaincode.signRequests(proposalInfo);
    }

    @Override
    public void createSignResponse(String proposalId, boolean reject, String reason) {
        ChannelConfigProposal proposal = getProposal(proposalId);
        CMSCCRequestBean proposalInfo = new CMSCCRequestBean(proposal.getChannelId());
        proposalInfo.setRequestId(proposalId);
        proposalInfo.setRequester(proposal.getRequester());
        if (reject) {
            proposalInfo.setReason(reason);
        } else {
            byte[] sign = channelMemberService.signUpdateConfigTransaction(proposal.getContent());
            proposalInfo.setSignature(sign);
        }
        memberManageChaincode.signResponses(proposalInfo);
    }

    @Override
    public void submitProposal(String proposalId) {
        ChannelConfigProposal proposal = getProposal(proposalId);
        if (!MemberManageChaincode.RequestState.SIGNING.getState().equals(proposal.getStatus())) {
            throw new ChannelServiceException(ChannelServiceException.PROPOSAL_CANNOT_BE_SUBMITTED, proposal.getStatus(), proposalId);
        }

        String channelId = proposal.getChannelId();
        byte[] updateConfigTrans = proposal.getContent();

        List<MemberManageChaincode.SignResponse> allSignResponses = memberManageChaincode.getAllSignResponses(channelId, proposalId);
        ArrayList<byte[]> signatures = new ArrayList<>();
        if (null != allSignResponses) {
            for (MemberManageChaincode.SignResponse signResponse : allSignResponses) {
                if (null != signResponse.getSignature()) {
                    byte[] signBytes = DatatypeConverter.parseHexBinary(signResponse.getSignature());
                    signatures.add(signBytes);
                }
            }
        }

        channelMemberService.submitConfigUpdate(channelId, updateConfigTrans, signatures);
        //fixme 暂时手动触发，最好做监听，然后自动触发
        networkService.resetNetwork();
    }

    @Override
    public void recallProposal(String proposalId) {
        ChannelConfigProposal proposal = getProposal(proposalId);
        if (MemberManageChaincode.RequestState.INVALID.getState().equals(proposal.getStatus())) {
            return;
        } else if (!MemberManageChaincode.RequestState.SIGNING.getState().equals(proposal.getStatus())) {
            throw new ChannelServiceException(ChannelServiceException.PROPOSAL_CANNOT_BE_RECALL, proposal.getStatus(), proposalId);
        }

        try {
            CMSCCRequestBean updateStateInfo = new CMSCCRequestBean(proposal.getChannelId());
            updateStateInfo.setRequestId(proposal.getProposalId());
            updateStateInfo.setState(MemberManageChaincode.RequestState.INVALID);
            memberManageChaincode.updateRequestState(updateStateInfo);
            channelConfigProposalDao.updateProposalStatus(proposal.getProposalId(), "invalid");
        } catch (Throwable e) {
            throw new ChannelServiceException(ChannelServiceException.PROPOSAL_RECALL_ERROR, e, proposalId);
        }
    }

    @Override
    public void deleteProposal(String proposalId) {
        channelConfigProposalDao.deleteProposal(proposalId);
    }
}
