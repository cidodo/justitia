package org.hyperledger.justitia.scheduler.service.proxy.chaincode;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.justitia.common.face.modules.chaincode.bean.TransactionRequestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChaincodeRequestService implements org.hyperledger.justitia.common.face.modules.chaincode.ChaincodeRequestService {
    private final org.hyperledger.justitia.common.face.modules.chaincode.ChaincodeRequestService chaincodeRequestService;

    @Autowired
    public ChaincodeRequestService(org.hyperledger.justitia.common.face.modules.chaincode.ChaincodeRequestService chaincodeRequestService) {
        this.chaincodeRequestService = chaincodeRequestService;
    }

    @Override
    public ProposalResponse invoke(TransactionRequestBean transactionRequestBean) throws Exception {
        return chaincodeRequestService.invoke(transactionRequestBean);
    }

    @Override
    public ProposalResponse query(TransactionRequestBean transactionRequestBean) throws Exception {
        return chaincodeRequestService.query(transactionRequestBean);
    }

    @Override
    public List<Query.ChaincodeInfo> queryInstalledChaincodes(String peerId) throws Exception {
        return chaincodeRequestService.queryInstalledChaincodes(peerId);
    }

    @Override
    public List<Query.ChaincodeInfo> queryInstantiatedChaincodes(String channelName, String peerId) throws Exception {
        return chaincodeRequestService.queryInstantiatedChaincodes(channelName, peerId);
    }
}
