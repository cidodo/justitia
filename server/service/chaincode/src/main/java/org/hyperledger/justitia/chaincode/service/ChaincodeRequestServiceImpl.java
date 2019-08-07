package org.hyperledger.justitia.chaincode.service;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.justitia.chaincode.exception.ChaincodeServiceException;
import org.hyperledger.justitia.common.face.service.chaincode.ChaincodeRequestService;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeInfo;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeInvokeResult;
import org.hyperledger.justitia.common.bean.chaincode.ChaincodeQueryResult;
import org.hyperledger.justitia.common.bean.chaincode.TransactionRequestBean;
import org.hyperledger.justitia.common.face.service.fabric.ChaincodeService;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.hyperledger.justitia.chaincode.exception.ChaincodeServiceException.*;

@Service
public class ChaincodeRequestServiceImpl implements ChaincodeRequestService {
    private final ChaincodeService chaincodeService;

    public ChaincodeRequestServiceImpl(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @Override
    public ChaincodeInvokeResult invoke(TransactionRequestBean transactionRequestBean){
        if (null == transactionRequestBean) {
            throw new IllegalArgumentException("Transaction request information is null.");
        }
        final String chaincodeName = transactionRequestBean.getChaincodeName();
        final String chaincodeVersion = transactionRequestBean.getChaincodeVersion();
        final String function = transactionRequestBean.getFunction();
        final String args = Arrays.toString(transactionRequestBean.getArgs().toArray());

        BlockEvent.TransactionEvent event;
        try {
            event = chaincodeService.invoke(transactionRequestBean);
        } catch (InvalidArgumentException | ProposalException e) {
            throw new ChaincodeServiceException(CHAINCODE_INVOKE_ERROR, chaincodeName, chaincodeVersion, function, args);
        }
        ChaincodeInvokeResult chaincodeInvokeResult = new ChaincodeInvokeResult(event);
        ChaincodeInfo chaincodeInfo = new ChaincodeInfo();
        chaincodeInfo.setName(chaincodeName);
        chaincodeInfo.setVersion(chaincodeVersion);
        chaincodeInfo.setInput(args);
        chaincodeInvokeResult.setChaincodeInfo(chaincodeInfo);
        return chaincodeInvokeResult;
    }

    @Override
    public Collection<ProposalResponse> endorsement(TransactionRequestBean transactionRequestBean) {
        if (null == transactionRequestBean) {
            throw new IllegalArgumentException("Transaction request information is null.");
        }
        final String chaincodeName = transactionRequestBean.getChaincodeName();
        final String chaincodeVersion = transactionRequestBean.getChaincodeVersion();
        final String function = transactionRequestBean.getFunction();
        final String args = Arrays.toString(transactionRequestBean.getArgs().toArray());

        try {
            return chaincodeService.endorsement(transactionRequestBean);
        } catch (ProposalException | InvalidArgumentException e) {
            throw new ChaincodeServiceException(CHAINCODE_ENDORSEMENT_ERROR, chaincodeName, chaincodeVersion, function, args);
        }
    }

    @Override
    public BlockEvent.TransactionEvent sendTransaction(String channelId, Collection<ProposalResponse> responses) {
        return chaincodeService.sendTransaction(channelId, responses);
    }

    @Override
    public ChaincodeQueryResult query(TransactionRequestBean transactionRequestBean) {
        if (null == transactionRequestBean) {
            throw new IllegalArgumentException("Transaction request information is null.");
        }
        final String chaincodeName = transactionRequestBean.getChaincodeName();
        final String chaincodeVersion = transactionRequestBean.getChaincodeVersion();
        final String function = transactionRequestBean.getFunction();
        final String args = Arrays.toString(transactionRequestBean.getArgs().toArray());

        Collection<ProposalResponse> responses;
        try {
            responses = chaincodeService.query(transactionRequestBean);
        } catch (ProposalException | InvalidArgumentException e) {
            throw new ChaincodeServiceException(CHAINCODE_QUERY_ERROR, chaincodeName, chaincodeVersion, function, args);
        }
        try {
            return new ChaincodeQueryResult(responses);
        } catch (InvalidArgumentException e) {
            throw new ChaincodeServiceException(CHAINCODE_RESULT_DATA_ERROR, e,
                    chaincodeName, chaincodeVersion, args, function);
        }
    }

    @Override
    public List<ChaincodeInfo> queryInstalledChaincodes(String peerId) {
        //fixme 通过事件监听，将监听到的合约信息缓存在内存中
        List<ChaincodeInfo> chaincodesInfo = new ArrayList<>();
        List<Query.ChaincodeInfo> result;
        try {
            result = chaincodeService.queryInstalledChaincodes(peerId);
        } catch (ProposalException | InvalidArgumentException e) {
           throw new ChaincodeServiceException(QUERY_INSTALLED_CHAINCODE_ERROR, peerId);
        }
        if (null != result && !result.isEmpty()) {
            for (Query.ChaincodeInfo res : result) {
                chaincodesInfo.add(formatChaincodeInfo(null, res));
            }
        }
        return chaincodesInfo;
    }

    @Override
    public List<ChaincodeInfo> queryInstantiatedChaincodes(String channelId, String peerId) {
        //fixme 通过事件监听，将监听到的合约信息缓存在内存中
        List<ChaincodeInfo> chaincodesInfo = new ArrayList<>();
        List<Query.ChaincodeInfo> result;
        try {
            result = chaincodeService.queryInstantiatedChaincodes(channelId, peerId);
        } catch (ProposalException | InvalidArgumentException e) {
            throw new ChaincodeServiceException(QUERY_INSTANTIATED_CHAINCODE_ERROR, channelId);
        }
        if (null != result && !result.isEmpty()) {
            for (Query.ChaincodeInfo res : result) {
                chaincodesInfo.add(formatChaincodeInfo(channelId, res));
            }
        }
        return chaincodesInfo;
    }

    private ChaincodeInfo formatChaincodeInfo(String channelId, Query.ChaincodeInfo info) {
        ChaincodeInfo chaincodeInfo = new ChaincodeInfo();
        chaincodeInfo.setId(info.getId().toString());
        chaincodeInfo.setName(info.getName());
        chaincodeInfo.setVersion(info.getVersion());
        chaincodeInfo.setLanguage(null);
        chaincodeInfo.setPath(info.getPath());
        chaincodeInfo.setInput(info.getInput());
        if (info.isInitialized()) {
            ChaincodeInfo.InstantiatedInfo instantiatedInfo = new ChaincodeInfo.InstantiatedInfo();
            instantiatedInfo.setEscc(info.getEscc());
            instantiatedInfo.setVscc(info.getVscc());
            instantiatedInfo.setEndorsementPolicy(null);
            chaincodeInfo.addInstantiatedInfo(channelId, instantiatedInfo);
        }
        return chaincodeInfo;
    }
}
