package org.hyperledger.justitia.common.bean.chaincode;

import com.google.protobuf.ByteString;
import lombok.Data;
import org.hyperledger.fabric.protos.peer.FabricProposalResponse;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.util.Collection;

@Data
public class ChaincodeQueryResult {
    private boolean success;
    private ChaincodeInfo chaincodeInfo;
    private ByteString payload;


    public ChaincodeQueryResult(Collection<ProposalResponse> responses) throws InvalidArgumentException {
        if (null == responses || responses.isEmpty()) {
            success = false;
            return;
        }

        ProposalResponse response = responses.iterator().next();
        chaincodeInfo = new ChaincodeInfo();
        ChaincodeID chaincodeID = response.getChaincodeID();
        chaincodeInfo.setName(chaincodeID.getName());
        chaincodeInfo.setVersion(chaincodeID.getVersion());
        chaincodeInfo.setPath(chaincodeID.getPath());



        for (ProposalResponse response : responses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                chaincodeInfo = new ChaincodeInfo();
                ChaincodeID chaincodeID = response.getChaincodeID();
                chaincodeInfo.setName(chaincodeID.getName());
                chaincodeInfo.setVersion(chaincodeID.getVersion());
                chaincodeInfo.setPath(chaincodeID.getPath());

                FabricProposalResponse.Response fabricProposalResponse = response.getProposalResponse().getResponse();
                if (fabricProposalResponse != null && fabricProposalResponse.getStatus() == 200) {
                    payload = fabricProposalResponse.getPayload();
                    break;
                }
            }
        }
    }

    public String getPayloadAsString() {
        if (null == payload) {
            return null;
        }
        return payload.toString();
    }


}
