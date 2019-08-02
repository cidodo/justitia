package org.hyperledger.justitia.chaincode.service;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.justitia.common.face.service.chaincode.ChaincodeManageService;
import org.hyperledger.justitia.common.bean.chaincode.TransactionRequestBean;
import org.hyperledger.justitia.common.face.service.fabric.ChaincodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
public class ChaincodeManageServiceImpl implements ChaincodeManageService {
    private final ChaincodeService chaincodeService;

    @Autowired
    public ChaincodeManageServiceImpl(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @Override
    public void install(TransactionRequestBean transactionRequestBean) {
        Collection<ProposalResponse> responses = chaincodeService.install(transactionRequestBean);

    }

    @Override
    public void instantiate(TransactionRequestBean transactionRequestBean) {
        chaincodeService.instantiate(transactionRequestBean);
    }

    @Override
    public void upgrade(TransactionRequestBean transactionRequestBean) {
        chaincodeService.upgrade(transactionRequestBean);
    }
}
