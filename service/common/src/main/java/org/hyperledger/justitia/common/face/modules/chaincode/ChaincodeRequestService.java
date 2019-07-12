package org.hyperledger.justitia.common.face.modules.chaincode;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.justitia.common.face.modules.chaincode.bean.TransactionRequestBean;

import java.util.List;

public interface ChaincodeRequestService {

    ProposalResponse invoke(TransactionRequestBean transactionRequestBean) throws Exception;

    ProposalResponse query(TransactionRequestBean transactionRequestBean) throws Exception;

    List<Query.ChaincodeInfo> queryInstalledChaincodes(String peerId) throws Exception;

    List<Query.ChaincodeInfo> queryInstantiatedChaincodes(String channelName, String peerId) throws Exception;

}
