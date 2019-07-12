package org.hyperledger.justitia.common.face.modules.chaincode;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.justitia.common.face.modules.chaincode.bean.TransactionRequestBean;

public interface ChaincodeManageService {

    ProposalResponse install(TransactionRequestBean transactionRequestBean) throws Exception;

    ProposalResponse instantiate(TransactionRequestBean transactionRequestBean) throws Exception;

    ProposalResponse upgrade(TransactionRequestBean transactionRequestBean) throws Exception;

}
