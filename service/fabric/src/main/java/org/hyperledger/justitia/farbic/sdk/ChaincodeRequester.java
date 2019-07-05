package org.hyperledger.justitia.farbic.sdk;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.justitia.farbic.exception.HFClientContextException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ChaincodeRequester {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChaincodeRequester.class);
    private final HFClientHelper clientHelper;
    private final HFChannelHelper channelHelper;

    @Autowired
    public ChaincodeRequester(HFClientHelper clientHelper, HFChannelHelper channelHelper) {
        this.clientHelper = clientHelper;
        this.channelHelper = channelHelper;
    }

    public List<Query.ChaincodeInfo> queryInstalledChaincodes(String peerId) throws ProposalException, InvalidArgumentException, HFClientContextException {
        if (null == peerId || peerId.isEmpty()) {
            LOGGER.warn("Peer id is empty.");
            return null;
        }
        HFClient client = clientHelper.getHFClient();
        if (null == client) {
            LOGGER.warn("Client is null.");
            return null;
        }
        Peer peer = clientHelper.createPeer(peerId);
        return client.queryInstalledChaincodes(peer);
    }

    public List<Query.ChaincodeInfo> queryInstantiatedChaincodes(String channelId, String peerId) throws ProposalException,
            InvalidArgumentException, TransactionException, HFClientContextException {

        Channel channel = channelHelper.createChannelNonOrderer(channelId, peerId);
        Collection<Peer> channelPeers = channel.getPeers();
        Peer peer = null;
        for (Peer channelPeer : channelPeers) {
            if (peerId.equals(channelPeer.getName())) {
                peer = channelPeer;
            }
        }
        return channel.queryInstantiatedChaincodes(peer);
    }

    public Collection<ProposalResponse> invokeChaincode(String channelId, String chaincodeName, String function, ArrayList<String> args)
            throws InvalidArgumentException, ProposalException, TransactionException, HFClientContextException {

        HFClient client = clientHelper.getHFClient();
        TransactionProposalRequest request = client.newTransactionProposalRequest();
        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                .setName(chaincodeName)
                .build();
        request.setChaincodeID(chaincodeID);
//        request.setProposalWaitTime(Config.getConfig().getProposalWaitTime());  //默认35000，也可以自定义
        request.setFcn(function);
        request.setArgs(args);
        //FIXME: I do not know the purpose of transient map works for.
        Map<String, byte[]> transientMap = new HashMap<>();
        Map<String, byte[]> tm2 = new HashMap<>();
        transientMap.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(Charset.forName("UTF8"))); //Just some extra junk in transient map
        transientMap.put("method", "InstantiateProposalRequest".getBytes(Charset.forName("UTF-8"))); // ditto
        tm2.put("result", ":)".getBytes(Charset.forName("UTF8")));  // This should be returned see chaincode why.
        tm2.put("event", "!".getBytes(Charset.forName("UTF8")));  //This should trigger an event see chaincode why.
        request.setTransientMap(transientMap);

        Channel channel = channelHelper.createChannel(channelId);
        return channel.sendTransactionProposal(request);
    }

    public Collection<ProposalResponse> queryChaincode(String channelName, String chaincodeName,String function, ArrayList<String> args){
        return null;
    }

    public CompletableFuture<BlockEvent.TransactionEvent> sendTransactionToOrderer(String channelId, Collection<ProposalResponse> responses)
            throws TransactionException, InvalidArgumentException, HFClientContextException {

        Channel channel = channelHelper.createChannel(channelId);
        return channel.sendTransaction(responses);
    }
}
