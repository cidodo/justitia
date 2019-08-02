package org.hyperledger.justitia.farbic.service;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.justitia.common.bean.chaincode.TransactionRequestBean;
import org.hyperledger.justitia.common.face.service.fabric.ChaincodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ChaincodeServiceImpl extends FabricServiceHelper implements ChaincodeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChaincodeServiceImpl.class);
    private final HFClientHelper clientHelper;

    @Autowired
    public ChaincodeServiceImpl(HFClientHelper clientHelper) {
        this.clientHelper = clientHelper;
    }

    @Override
    public Collection<ProposalResponse> install(TransactionRequestBean transactionRequestBean) throws InvalidArgumentException, ProposalException {
        HFClient client = clientHelper.getHFClient();
        Set<Peer> peers = clientHelper.createPeers(client, transactionRequestBean.getPeerNames());
        if (peers.isEmpty()) {
            throw new IllegalArgumentException("Install chaincode failed: at least one peer needed");
        }

        InstallProposalRequest proposalRequest = client.newInstallProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);
        return client.sendInstallProposal(proposalRequest, peers);
    }

    @Override
    public void instantiate(TransactionRequestBean transactionRequestBean) throws ProposalException, InvalidArgumentException {
        HFClient client = clientHelper.getHFClient();
        Channel channel = clientHelper.createChannel(client, transactionRequestBean.getChannelName());

        InstantiateProposalRequest proposalRequest = client.newInstantiationProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);

        Collection<Peer> peers = getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.isEmpty()) {
            throw new IllegalArgumentException("Instantiate chaincode failed: at least one peer needed");
        }

        Collection<ProposalResponse> proposalResponses = channel.sendInstantiationProposal(proposalRequest, peers);
        Set<ProposalResponse> successProposal = checkProposalResponses(proposalResponses);

        CompletableFuture<BlockEvent.TransactionEvent> future = channel.sendTransaction(successProposal, channel.getOrderers());
        getTransactionResult(future);
        channel.shutdown(true);
    }

    @Override
    public void upgrade(TransactionRequestBean transactionRequestBean) throws ProposalException, InvalidArgumentException {
        HFClient client = clientHelper.getHFClient();
        Channel channel = clientHelper.createChannel(client, transactionRequestBean.getChannelName());

        UpgradeProposalRequest proposalRequest = client.newUpgradeProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);
        proposalRequest.setFcn("init");
        proposalRequest.setArgs(new String[0]);

        Collection<Peer> peers = getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.isEmpty()) {
            throw new IllegalArgumentException("Upgrade chaincode failed: at least one peer needed");
        }

        Collection<ProposalResponse> proposalResponses = channel.sendUpgradeProposal(proposalRequest, peers);
        Set<ProposalResponse> successProposal = checkProposalResponses(proposalResponses);

        CompletableFuture<BlockEvent.TransactionEvent> future = channel.sendTransaction(successProposal, channel.getOrderers());
        getTransactionResult(future);
        channel.shutdown(true);
    }

    @Override
    public BlockEvent.TransactionEvent invoke(TransactionRequestBean transactionRequestBean) throws InvalidArgumentException, ProposalException {
        HFClient client = clientHelper.getHFClient();
        Channel channel = clientHelper.createChannel(client, transactionRequestBean.getChannelName());

        TransactionProposalRequest proposalRequest = client.newTransactionProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);

        Collection<Peer> peers = getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.size() == 0) {
            throw new IllegalArgumentException("invoke transaction failed: at least one peer needed");
        }
        Collection<ProposalResponse> proposalResponses = channel.sendTransactionProposal(proposalRequest, peers);
        Set<ProposalResponse> successProposal = checkProposalResponses(proposalResponses);

        CompletableFuture<BlockEvent.TransactionEvent> future = channel.sendTransaction(successProposal, channel.getOrderers());
        BlockEvent.TransactionEvent transactionResult = getTransactionResult(future);
        channel.shutdown(true);

        return transactionResult;
    }

    @Override
    public Collection<ProposalResponse> endorsement(TransactionRequestBean transactionRequestBean) throws ProposalException, InvalidArgumentException {
        HFClient client = clientHelper.getHFClient();
        Channel channel = clientHelper.createChannel(client, transactionRequestBean.getChannelName());

        TransactionProposalRequest proposalRequest = client.newTransactionProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);

        Collection<Peer> peers = getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.size() == 0) {
            throw new IllegalArgumentException("invoke transaction failed: at least one peer needed");
        }

        channel.shutdown(true);
        return channel.sendTransactionProposal(proposalRequest, peers);
    }

    @Override
    public BlockEvent.TransactionEvent sendTransaction(String channelId, Collection<ProposalResponse> responses) {
        HFClient client = clientHelper.getHFClient();
        Channel channel = clientHelper.createChannel(client, channelId);
        Set<ProposalResponse> successProposal = checkProposalResponses(responses);

        CompletableFuture<BlockEvent.TransactionEvent> future = channel.sendTransaction(successProposal, channel.getOrderers());
        BlockEvent.TransactionEvent transactionResult = getTransactionResult(future);

        channel.shutdown(true);
        return transactionResult;
    }

    @Override
    public Collection<ProposalResponse> query(TransactionRequestBean transactionRequestBean) throws ProposalException, InvalidArgumentException {
        HFClient client = clientHelper.getHFClient();
        Channel channel = clientHelper.createChannel(client, transactionRequestBean.getChannelName());

        QueryByChaincodeRequest proposalRequest = client.newQueryProposalRequest();
        transactionRequestBean.toRequest(proposalRequest);

        Collection<Peer> peers = getPeersWithChannel(transactionRequestBean.getPeerNames(), channel);
        if (peers.size() == 0) {
            throw new IllegalArgumentException("query transaction failed: at least one peer needed");
        }
        Collection<ProposalResponse> proposalResponses = channel.queryByChaincode(proposalRequest, peers);

        channel.shutdown(true);
        return proposalResponses;
    }

    @Override
    public List<Query.ChaincodeInfo> queryInstalledChaincodes(String peerId) throws ProposalException, InvalidArgumentException {
        HFClient client = clientHelper.getHFClient();
        Peer peer = clientHelper.createPeer(client, peerId);
        return client.queryInstalledChaincodes(peer);
    }

    public Map<String, List<Query.ChaincodeInfo>> queryInstalledChaincodes(Set<String> peersId) {
        if (null == peersId || peersId.isEmpty()) {
            throw new IllegalArgumentException("Peers is empty.");
        }
        Map<String, List<Query.ChaincodeInfo>> chaincodeInfoMap = new HashMap<>();
        HFClient client = clientHelper.getHFClient();
        Set<Peer> peers = clientHelper.createPeers(client, peersId);
        if (null == peers || peers.isEmpty()) {
            throw new IllegalArgumentException(String.format("Cannot get any peer in [%s].", Arrays.toString(peersId.toArray())));
        }
        for (Peer peer : peers) {
            List<Query.ChaincodeInfo> chaincodeInfos = null;
            try {
                chaincodeInfos = client.queryInstalledChaincodes(peer);
            } catch (InvalidArgumentException | ProposalException e) {
                LOGGER.error("Query installed chaincode failed by peer {}.", peer.getName(), e);
            }
            chaincodeInfoMap.put(peer.getName(), chaincodeInfos);
        }
        return chaincodeInfoMap;
    }

    @Override
    public List<Query.ChaincodeInfo> queryInstantiatedChaincodes(String channelId, String peerId) throws ProposalException, InvalidArgumentException {
        Channel channel = clientHelper.createChannelWithAdmin(channelId);
        ArrayList<String> peersId = new ArrayList<>();
        peersId.add(peerId);
        Collection<Peer> peers = getPeersWithChannel(peersId, channel);
        if (peers == null || peers.isEmpty()) {
            throw new IllegalArgumentException("Cannot get peer with peerName:" + peerId + " in channel:" + channelId);
        }
        List<Query.ChaincodeInfo> chaincodeInfos = channel.queryInstantiatedChaincodes(peers.iterator().next());

        channel.shutdown(true);
        return chaincodeInfos;
    }

    public Map<String, List<Query.ChaincodeInfo>> queryInstantiatedChaincodes(String channelId, Set<String> peersId) {
        if (null == peersId || peersId.isEmpty()) {
            throw new IllegalArgumentException("Peers is empty.");
        }
        Map<String, List<Query.ChaincodeInfo>> chaincodeInfoMap = new HashMap<>();
        Channel channel = clientHelper.createChannelWithAdmin(channelId);
        Collection<Peer> peers = getPeersWithChannel(new ArrayList<>(peersId), channel);
        if (peers == null || peers.isEmpty()) {
            throw new IllegalArgumentException("Cannot get any peer in channel:" + channelId);
        }
        for (Peer peer : peers) {
            List<Query.ChaincodeInfo> chaincodeInfos = null;
            try {
                chaincodeInfos = channel.queryInstantiatedChaincodes(peers.iterator().next());
            } catch (InvalidArgumentException | ProposalException e) {
                LOGGER.error("Query instantiated chaincode failed by peer {}.", peer.getName(), e);
            }
            chaincodeInfoMap.put(peer.getName(), chaincodeInfos);
        }

        channel.shutdown(true);
        return chaincodeInfoMap;
    }
}
