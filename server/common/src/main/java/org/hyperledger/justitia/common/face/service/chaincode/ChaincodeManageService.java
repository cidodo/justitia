package org.hyperledger.justitia.common.face.service.chaincode;

import org.hyperledger.justitia.common.bean.chaincode.TransactionRequestBean;

public interface ChaincodeManageService {

    void install(TransactionRequestBean transactionRequestBean);

    void instantiate(TransactionRequestBean transactionRequestBean);

    void upgrade(TransactionRequestBean transactionRequestBean);

}
