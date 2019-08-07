package org.hyperledger.justitia.chaincode.service;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.justitia.chaincode.exception.ChaincodeServiceException;
import org.hyperledger.justitia.common.face.service.chaincode.ChaincodeManageService;
import org.hyperledger.justitia.common.bean.chaincode.TransactionRequestBean;
import org.hyperledger.justitia.common.face.service.fabric.ChaincodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.hyperledger.justitia.chaincode.exception.ChaincodeServiceException.*;


@Service
public class ChaincodeManageServiceImpl implements ChaincodeManageService {
    private final ChaincodeService chaincodeService;

    @Autowired
    public ChaincodeManageServiceImpl(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @Override
    public void install(TransactionRequestBean transactionRequestBean) {
        if (null == transactionRequestBean) {
            throw new IllegalArgumentException("Transaction request is null.");
        }
        String chaincodeName = transactionRequestBean.getChaincodeName();
        String chaincodeVersion = transactionRequestBean.getChaincodeVersion();
        ArrayList<String> args = transactionRequestBean.getArgs();
        String function = transactionRequestBean.getFunction();

        Collection<ProposalResponse> responses;
        try {
            responses = chaincodeService.install(transactionRequestBean);
        } catch (ProposalException | InvalidArgumentException e) {
            throw new ChaincodeServiceException(CHAINCODE_INSTALL_ERROR, e, chaincodeName, chaincodeVersion);
        }

        Set<ProposalResponse> invalid = new HashSet<>();
        ChaincodeServiceHelper.checkProposalResponses(responses, invalid, chaincodeName, chaincodeVersion, Arrays.toString(args.toArray()), function);

        if (!invalid.isEmpty()) {
            ArrayList<String> failedPeers = new ArrayList<>();
            for (ProposalResponse res : invalid) {
                failedPeers.add(res.getPeer().getName());
            }
            throw new ChaincodeServiceException(CHAINCODE_INSTALL_FAILED, chaincodeName, chaincodeVersion, Arrays.toString(failedPeers.toArray()));
        }
    }

    @Override
    public void instantiate(TransactionRequestBean transactionRequestBean) {
        if (null == transactionRequestBean) {
            throw new IllegalArgumentException("Transaction request is null.");
        }
        String chaincodeName = transactionRequestBean.getChaincodeName();
        String chaincodeVersion = transactionRequestBean.getChaincodeVersion();

        try {
            chaincodeService.instantiate(transactionRequestBean);
        } catch (ProposalException | InvalidArgumentException e) {
            throw new ChaincodeServiceException(CHAINCODE_INSTANTIATE_ERROR, e, chaincodeName, chaincodeVersion);
        }
    }

    @Override
    public void upgrade(TransactionRequestBean transactionRequestBean) {
        if (null == transactionRequestBean) {
            throw new IllegalArgumentException("Transaction request is null.");
        }
        String chaincodeName = transactionRequestBean.getChaincodeName();
        String chaincodeVersion = transactionRequestBean.getChaincodeVersion();

        try {
            chaincodeService.upgrade(transactionRequestBean);
        } catch (ProposalException | InvalidArgumentException e) {
            throw new ChaincodeServiceException(CHAINCODE_UPGRADE_ERROR, e, chaincodeName, chaincodeVersion);
        }
    }
}
