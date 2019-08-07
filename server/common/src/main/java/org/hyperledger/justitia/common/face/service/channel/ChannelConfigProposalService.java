package org.hyperledger.justitia.common.face.service.channel;

import org.hyperledger.justitia.common.bean.channel.ChannelConfigProposal;

import java.util.List;

public interface ChannelConfigProposalService {
    List<ChannelConfigProposal> getProposals();
    List<ChannelConfigProposal> getProposals(String expectedState);
    ChannelConfigProposal getProposal(String proposalId);
    void createSignResponse(String proposalId, boolean reject, String reason);
    void submitProposal(String proposalId);
    void recallProposal(String proposalId);
    void deleteProposal(String proposalId);
}
