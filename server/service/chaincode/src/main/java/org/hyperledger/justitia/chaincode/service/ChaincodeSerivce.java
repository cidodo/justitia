package org.hyperledger.justitia.chaincode.service;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.justitia.chaincode.exception.ChaincodeServiceException;

import java.util.Collection;
import java.util.Set;

public class ChaincodeSerivce {

    Set<ProposalResponse> checkProposalResponses(Collection<ProposalResponse> responses, Set<ProposalResponse> invalid) {
        Collection<Set<ProposalResponse>> consistencySets;
        try {
            consistencySets = SDKUtils.getProposalConsistencySets(responses, invalid);
        } catch (InvalidArgumentException e) {
            String cause = e.getMessage();
            throw new ChaincodeServiceException(FabricServiceException.ENDORSER_FAILED, e, cause);
        }
        if (consistencySets.size() != 1) {
            String cause = "Expected only one set of consistent proposal responses but got " + consistencySets.size();
            throw new ChaincodeServiceException(FabricServiceException.ENDORSER_FAILED, cause);
        }

        return consistencySets.iterator().next();
    }
}
