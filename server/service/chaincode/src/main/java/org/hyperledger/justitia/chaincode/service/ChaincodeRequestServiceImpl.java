package org.hyperledger.justitia.chaincode.service;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.justitia.common.face.service.chaincode.ChaincodeRequestService;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeInfo;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeInvokeResult;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeQueryResult;
import org.hyperledger.justitia.common.bean.chaincode.TransactionRequestBean;
import org.hyperledger.justitia.common.face.service.fabric.ChaincodeService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChaincodeRequestServiceImpl implements ChaincodeRequestService {
    private final ChaincodeService chaincodeService;

    public ChaincodeRequestServiceImpl(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @Override
    public ChaincodeInvokeResult invoke(TransactionRequestBean transactionRequestBean){
        BlockEvent.TransactionEvent event = chaincodeService.invoke(transactionRequestBean);
        return new ChaincodeInvokeResult(event);
    }

    @Override
    public Collection<ProposalResponse> endorsement(TransactionRequestBean transactionRequestBean) throws ProposalException, InvalidArgumentException {
        return chaincodeService.endorsement(transactionRequestBean);
    }

    @Override
    public BlockEvent.TransactionEvent sendTransaction(String channelId, Collection<ProposalResponse> responses) {
        return chaincodeService.sendTransaction(channelId, responses);
    }

    @Override
    public ChaincodeQueryResult query(TransactionRequestBean transactionRequestBean) {
        Collection<ProposalResponse> responses = chaincodeService.query(transactionRequestBean);
        return new ChaincodeQueryResult(responses);
    }

    @Override
    public List<ChaincodeInfo> queryInstalledChaincodes(String peerId) {
        List<ChaincodeInfo> chaincodesInfo = new ArrayList<>();
        List<Query.ChaincodeInfo> chaincodeInfos = chaincodeService.queryInstalledChaincodes(peerId);
        if (null != chaincodeInfos && !chaincodeInfos.isEmpty()) {
            for (Query.ChaincodeInfo chaincodeInfo : chaincodeInfos) {
                chaincodesInfo.add(new ChaincodeInfo(chaincodeInfo));
            }
        }
        return chaincodesInfo;
    }

    @Override
    public List<ChaincodeInfo> queryInstantiatedChaincodes(String channelName, String peerId) {
        List<ChaincodeInfo> chaincodesInfo = new ArrayList<>();
        List<Query.ChaincodeInfo> chaincodeInfos = chaincodeService.queryInstantiatedChaincodes(channelName, peerId);
        if (null != chaincodeInfos && !chaincodeInfos.isEmpty()) {
            for (Query.ChaincodeInfo chaincodeInfo : chaincodeInfos) {
                chaincodesInfo.add(new ChaincodeInfo(chaincodeInfo));
            }
        }
        return chaincodesInfo;
    }
}
