package org.hyperledger.justitia.channel.dao;

import org.hyperledger.justitia.common.bean.channel.ChannelConfigProposal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelConfigProposalDao {
    public ChannelConfigProposal getProposal(String proposalId) {
        return null;
    }

    public List<ChannelConfigProposal> selectProposal() {
        return null;
    }

    public List<ChannelConfigProposal> selectProposalByStatus(String status) {
        return null;
    }

    public int updateProposalStatus(String proposalId, String status) {
        return 0;
    }

    public int deleteProposal(String proposalId) {
        return 0;
    }
}