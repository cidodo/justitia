package org.hyperledger.justitia.chaincode.service;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.justitia.chaincode.service.bean.TransactionRequestBean;

public interface ChaincodeManageService {

    ProposalResponse install(TransactionRequestBean transactionRequestBean) throws Exception;

    ProposalResponse instantiate(TransactionRequestBean transactionRequestBean) throws Exception;

    ProposalResponse upgrade(TransactionRequestBean transactionRequestBean) throws Exception;

}
