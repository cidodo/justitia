package org.hyperledger.justitia.chaincode.service;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.justitia.chaincode.exception.ChaincodeServiceException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.hyperledger.justitia.chaincode.exception.ChaincodeServiceException.CHAINCODE_RESULT_DATA_ERROR;

public class ChaincodeServiceHelper {

    static Set<ProposalResponse> checkProposalResponses(Collection<ProposalResponse> responses, Set<ProposalResponse> invalid,
                                                        String chaincodeId, String version, String args, String function) {
        Collection<Set<ProposalResponse>> consistencySets;
        try {
            consistencySets = SDKUtils.getProposalConsistencySets(responses, invalid);
        } catch (InvalidArgumentException e) {
            throw new ChaincodeServiceException(CHAINCODE_RESULT_DATA_ERROR, e,
                    chaincodeId, version, args, function);
        }
        if (consistencySets.size() != 1) {
            String cause = "Expected only one set of consistent proposal responses but got " + consistencySets.size();
            throw new ChaincodeServiceException(CHAINCODE_RESULT_DATA_ERROR, new InvalidArgumentException(cause),
                    chaincodeId, version, args, function);
        }
        return consistencySets.iterator().next();
    }
}
