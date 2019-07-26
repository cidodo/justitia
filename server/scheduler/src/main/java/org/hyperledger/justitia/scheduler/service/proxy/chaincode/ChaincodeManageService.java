package org.hyperledger.justitia.scheduler.service.proxy.chaincode;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.justitia.service.face.chaincode.bean.TransactionRequestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChaincodeManageService implements org.hyperledger.justitia.service.face.chaincode.ChaincodeManageService {
    private final org.hyperledger.justitia.service.face.chaincode.ChaincodeManageService chaincodeManageService;

    @Autowired
    public ChaincodeManageService(org.hyperledger.justitia.service.face.chaincode.ChaincodeManageService chaincodeManageService) {
        this.chaincodeManageService = chaincodeManageService;
    }

    @Override
    public ProposalResponse install(TransactionRequestBean transactionRequestBean) throws Exception {
        return chaincodeManageService.install(transactionRequestBean);
    }

    @Override
    public ProposalResponse instantiate(TransactionRequestBean transactionRequestBean) throws Exception {
        return chaincodeManageService.instantiate(transactionRequestBean);
    }

    @Override
    public ProposalResponse upgrade(TransactionRequestBean transactionRequestBean) throws Exception {
        return chaincodeManageService.upgrade(transactionRequestBean);
    }
}
