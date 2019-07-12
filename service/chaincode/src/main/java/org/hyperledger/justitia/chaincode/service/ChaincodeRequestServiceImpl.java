package org.hyperledger.justitia.chaincode.service;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.justitia.common.face.modules.chaincode.ChaincodeRequestService;
import org.hyperledger.justitia.common.face.modules.chaincode.bean.TransactionRequestBean;
import org.hyperledger.justitia.chaincode.service.util.ChaincodeUtil;
import org.hyperledger.justitia.farbic.sdk.HFChannelHelper;
import org.hyperledger.justitia.farbic.sdk.HFClientHelper;
import org.hyperledger.justitia.common.face.modules.identity.read.NodeReader;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ChaincodeRequestServiceImpl implements ChaincodeRequestService {

    private final HFClientHelper clientHelper;
    private final HFChannelHelper channelHelper;
    private final NodeReader  nodeReader;

    public ChaincodeRequestServiceImpl(HFClientHelper clientHelper, HFChannelHelper channelHelper, NodeReader nodeReader) {
        this.clientHelper = clientHelper;
        this.channelHelper = channelHelper;
        this.nodeReader = nodeReader;
    }

    @Override
    public ProposalResponse invoke(TransactionRequestBean transactionRequestBean) throws Exception {
        HFClient client = clientHelper.getHFClient();
        Channel channel = channelHelper.createChannel(transactionRequestBean.getChannelName());

        TransactionProposalRequest proposalRequest = client.newTransactionProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);

        Collection<Peer> peers = ChaincodeUtil.getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.size() == 0) {
            throw new IllegalArgumentException("invoke transaction failed: at least one peer needed");
        }
        Collection<ProposalResponse> proposalResponses = channel.sendTransactionProposal(proposalRequest, peers);
        ChaincodeUtil.checkProposalResponses(proposalResponses, true);

        BlockEvent.TransactionEvent event = channel.sendTransaction(proposalResponses).get();
        if (event == null || !event.isValid()) {
            throw new IllegalStateException("event is invalid");
        }

        return proposalResponses.iterator().next();
    }

    @Override
    public ProposalResponse query(TransactionRequestBean transactionRequestBean) throws Exception {
        HFClient client = clientHelper.getHFClient();
        Channel channel = channelHelper.createChannel(transactionRequestBean.getChannelName());

        QueryByChaincodeRequest proposalRequest = client.newQueryProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);

        Collection<Peer> peers = ChaincodeUtil.getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.size() == 0) {
            throw new IllegalArgumentException("query transaction failed: at least one peer needed");
        }
        Collection<ProposalResponse> proposalResponses = channel.queryByChaincode(proposalRequest, peers);
        ChaincodeUtil.checkProposalResponses(proposalResponses, true);

        return proposalResponses.iterator().next();
    }

    @Override
    public List<Query.ChaincodeInfo> queryInstalledChaincodes(String peerId) throws Exception {
        HFClient client = clientHelper.getHFClient();
        Peer peer = clientHelper.createPeer(peerId);
        return client.queryInstalledChaincodes(peer);
    }

    @Override
    public List<Query.ChaincodeInfo> queryInstantiatedChaincodes(String channelName, String peerId) throws Exception {
        Channel channel = channelHelper.createChannel(channelName);
        Peer peer = null;
        for (Peer p : channel.getPeers()) {
            if (p.getName().equals(peerId)) {
                peer = p;
                break;
            }
        }
        if (peer == null) {
            throw new IllegalArgumentException("Cannot get peer with peerName:" + peerId + " in channel:" + channelName);
        }

        return channel.queryInstantiatedChaincodes(peer);
    }
}
