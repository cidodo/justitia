package org.hyperledger.justitia.common.face.service.chaincode;

import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeInfo;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeInvokeResult;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeQueryResult;
import org.hyperledger.justitia.common.bean.chaincode.TransactionRequestBean;

import java.util.Collection;
import java.util.List;

public interface ChaincodeRequestService {

    ChaincodeInvokeResult invoke(TransactionRequestBean transactionRequestBean);

    Collection<ProposalResponse> endorsement(TransactionRequestBean transactionRequestBean);

    BlockEvent.TransactionEvent sendTransaction(String channelId, Collection<ProposalResponse> responses);

    ChaincodeQueryResult query(TransactionRequestBean transactionRequestBean);

    List<ChaincodeInfo> queryInstalledChaincodes(String peerId);

    List<ChaincodeInfo> queryInstantiatedChaincodes(String channelId, String peerId);

}
