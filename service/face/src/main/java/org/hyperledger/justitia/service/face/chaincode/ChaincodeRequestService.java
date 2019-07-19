package org.hyperledger.justitia.service.face.chaincode;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.justitia.service.face.chaincode.bean.TransactionRequestBean;

import java.util.Collection;
import java.util.List;

public interface ChaincodeRequestService {

    BlockEvent.TransactionEvent invoke(TransactionRequestBean transactionRequestBean) throws InvalidArgumentException, ProposalException;

    Collection<ProposalResponse> query(TransactionRequestBean transactionRequestBean) throws ProposalException, InvalidArgumentException;

    List<Query.ChaincodeInfo> queryInstalledChaincodes(String peerId) throws ProposalException, InvalidArgumentException;

    List<Query.ChaincodeInfo> queryInstantiatedChaincodes(String channelName, String peerId) throws ProposalException, InvalidArgumentException;

}
