package org.hyperledger.justitia.chaincode.service.impl;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.justitia.chaincode.service.ChaincodeManageService;
import org.hyperledger.justitia.chaincode.service.bean.TransactionRequestBean;
import org.hyperledger.justitia.chaincode.service.util.ChaincodeUtil;
import org.hyperledger.justitia.farbic.sdk.HFChannelHelper;
import org.hyperledger.justitia.farbic.sdk.HFClientHelper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChaincodeManageServiceImpl implements ChaincodeManageService {

    private final HFClientHelper clientHelper;
    private final HFChannelHelper channelHelper;

    public ChaincodeManageServiceImpl(HFClientHelper clientHelper, HFChannelHelper channelHelper) {
        this.clientHelper = Objects.requireNonNull(clientHelper);
        this.channelHelper = channelHelper;
    }


    @Override
    public ProposalResponse install(TransactionRequestBean transactionRequestBean) throws Exception {
        HFClient client = clientHelper.getHFClient();
        Channel channel = channelHelper.createChannel(transactionRequestBean.getChannelName());

        InstallProposalRequest proposalRequest = client.newInstallProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);

        Collection<Peer> peers = ChaincodeUtil.getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.isEmpty()) {
            throw new IllegalArgumentException("Install chaincode failed: at least one peer needed");
        }

        Collection<ProposalResponse> proposalResponses = client.sendInstallProposal(proposalRequest, peers);
        ChaincodeUtil.checkProposalResponses(proposalResponses, false);

        return proposalResponses.iterator().next();
    }


    @Override
    public ProposalResponse instantiate(TransactionRequestBean transactionRequestBean) throws Exception {
        HFClient client = clientHelper.getHFClient();
        Channel channel = channelHelper.createChannel(transactionRequestBean.getChannelName());

        InstantiateProposalRequest proposalRequest = client.newInstantiationProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);

        Collection<Peer> peers = ChaincodeUtil.getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.isEmpty()) {
            throw new IllegalArgumentException("Instantiate chaincode failed: at least one peer needed");
        }

        Collection<ProposalResponse> proposalResponses = channel.sendInstantiationProposal(proposalRequest, peers);
        ChaincodeUtil.checkProposalResponses(proposalResponses, true);

        BlockEvent.TransactionEvent event = channel.sendTransaction(proposalResponses, channel.getOrderers()).get();
        if (!event.isValid()) {
            throw new IllegalStateException("event is invalid");
        }

        return proposalResponses.iterator().next();
    }

    @Override
    public ProposalResponse upgrade(TransactionRequestBean transactionRequestBean) throws Exception {
        HFClient client = clientHelper.getHFClient();
        Channel channel = channelHelper.createChannel(transactionRequestBean.getChannelName());

        UpgradeProposalRequest proposalRequest = client.newUpgradeProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);
        proposalRequest.setFcn("init");
        proposalRequest.setArgs(new String[0]);

        Collection<Peer> peers = ChaincodeUtil.getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.isEmpty()) {
            throw new IllegalArgumentException("Upgrade chaincode failed: at least one peer needed");
        }

        Collection<ProposalResponse> proposalResponses = channel.sendUpgradeProposal(proposalRequest, peers);
        ChaincodeUtil.checkProposalResponses(proposalResponses, true);

        BlockEvent.TransactionEvent event = channel.sendTransaction(proposalResponses, channel.getOrderers()).get();

        if (!event.isValid()) {
            throw new IllegalStateException("event is invalid");
        }
        return proposalResponses.iterator().next();
    }

}
