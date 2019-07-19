package org.hyperledger.justitia.service.face.chaincode;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.justitia.service.face.chaincode.bean.TransactionRequestBean;

import java.util.Collection;

public interface ChaincodeManageService {

    Collection<ProposalResponse> install(TransactionRequestBean transactionRequestBean) throws InvalidArgumentException, ProposalException;

    void instantiate(TransactionRequestBean transactionRequestBean) throws ProposalException, InvalidArgumentException;

    void upgrade(TransactionRequestBean transactionRequestBean) throws ProposalException, InvalidArgumentException;

}
