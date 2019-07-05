package org.hyperledger.justitia.chaincode.service.util;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.SDKUtils;

import java.util.*;

public class ChaincodeUtil {

    public static Collection<Peer> getPeersWithChannel(List<String> peerNames, Channel channel) {
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

    //处理背书结果，判断结果是否异常
    public static void checkProposalResponses(Collection<ProposalResponse> responses, boolean consistency) throws Exception {
        List<ProposalResponse> success = new LinkedList<>();
        List<ProposalResponse> fail = new LinkedList<>();
        for (ProposalResponse response : responses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                success.add(response);
            } else {
                fail.add(response);
            }
        }

        if (fail.size() > 0) {
            throw new Exception(String.format("Not enough endorsers for invoke: %s", fail.get(0).getMessage()));
        }

        if (consistency) {
            Collection<Set<ProposalResponse>> consistencySets = SDKUtils.getProposalConsistencySets(responses);
            if (consistencySets.size() != 1) {
                throw new Exception("Expected only one set of consistent proposal responses but got " + consistencySets.size());
            }
        }

    }

}
