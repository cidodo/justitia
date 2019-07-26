package org.hyperledger.justitia.farbic.service;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.justitia.farbic.exception.FabricServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FabricServiceHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FabricServiceHelper.class);

    Collection<Peer> getPeersWithChannel(List<String> peerNames, Channel channel) {
        List<Peer> peers = new ArrayList<>();
        for (String name : peerNames) {
            boolean found = false;
            for (Peer peer : channel.getPeers()) {
                if (peer.getName().equals(name)) {
                    found = peers.add(peer);
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Cannot get peer with name: " + name + " in channel:" + channel.getName());
            }
        }
        return peers;
    }

    Set<ProposalResponse> checkProposalResponses(Collection<ProposalResponse> responses) {
        Set<ProposalResponse> invalid = new HashSet<>();
        Collection<Set<ProposalResponse>> consistencySets;
        try {
            consistencySets = SDKUtils.getProposalConsistencySets(responses, invalid);
        } catch (InvalidArgumentException e) {
            String cause = e.getMessage();
            throw new FabricServiceException(FabricServiceException.ENDORSER_FAILED, e, cause);
        }
        if (consistencySets.size() != 1) {
            String cause = "Expected only one set of consistent proposal responses but got " + consistencySets.size();
            throw new FabricServiceException(FabricServiceException.ENDORSER_FAILED, cause);
        }

        if (invalid.size() > 0) {
            LOGGER.warn("There are {} invalid endorsements:{}", invalid.size(), invalid.iterator().next().getMessage());
        }

        Set<ProposalResponse> success = consistencySets.iterator().next();
        if (success.isEmpty()) {
            String cause = "There is no effective endorsement.";
            throw new FabricServiceException(FabricServiceException.ENDORSER_FAILED, cause);
        }

        return success;
    }

    BlockEvent.TransactionEvent getTransactionResult(CompletableFuture<BlockEvent.TransactionEvent> future) {
        BlockEvent.TransactionEvent transactionEvent;
        try {
            transactionEvent = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new FabricServiceException(FabricServiceException.WAIT_RESULT_EXCEPTION, e);
        }

        if (!transactionEvent.isValid()) {
            throw new FabricServiceException(FabricServiceException.INVALID_TRANSACTION, transactionEvent.getTransactionID());
        }
        return transactionEvent;
    }

    void shutdownChannel (Channel channel, boolean force) {
        if (null != channel) {
            channel.shutdown(force);
        }
    }
}
