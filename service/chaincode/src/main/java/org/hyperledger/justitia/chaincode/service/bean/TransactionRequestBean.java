package org.hyperledger.justitia.chaincode.service.bean;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionRequestBean {

    private String chaincodeName;
    private String chaincodeVersion;
    private String chaincodePath;

    private String language;
    private String function;

    private ArrayList<String> args;
    private Map<String, byte[]> transientMap;
    private InputStream chaincodeInputStream;
    private File chaincodeSourceLocation;
    private ChaincodeEndorsementPolicy endorsementPolicy;

    private String channelName;
    private List<String> peerNames;

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public String getChaincodeVersion() {
        return chaincodeVersion;
    }

    public void setChaincodeVersion(String chaincodeVersion) {
        this.chaincodeVersion = chaincodeVersion;
    }

    public String getChaincodePath() {
        return chaincodePath;
    }

    public void setChaincodePath(String chaincodePath) {
        this.chaincodePath = chaincodePath;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }

    public InputStream getChaincodeInputStream() {
        return chaincodeInputStream;
    }

    public void setChaincodeInputStream(InputStream chaincodeInputStream) {
        this.chaincodeInputStream = chaincodeInputStream;
    }

    public File getChaincodeSourceLocation() {
        return chaincodeSourceLocation;
    }

    public void setChaincodeSourceLocation(File chaincodeSourceLocation) {
        this.chaincodeSourceLocation = chaincodeSourceLocation;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<String> getPeerNames() {
        return peerNames;
    }

    public void setPeerNames(List<String> peerNames) {
        this.peerNames = peerNames;
    }

    public Map<String, byte[]> getTransientMap() {
        return transientMap;
    }

    public void setTransientMap(Map<String, byte[]> transientMap) {
        this.transientMap = transientMap;
    }

    public ChaincodeEndorsementPolicy getEndorsementPolicy() {
        return endorsementPolicy;
    }

    public void setEndorsementPolicy(ChaincodeEndorsementPolicy endorsementPolicy) {
        this.endorsementPolicy = endorsementPolicy;
    }

    public void toRequest(TransactionRequest request) {
        ChaincodeID.Builder builder = ChaincodeID.newBuilder().setName(chaincodeName);
        if (chaincodeVersion != null) {
            builder.setVersion(chaincodeVersion);
        }
        if (chaincodePath != null) {
            builder.setPath(chaincodePath);
        }
        request.setChaincodeID(builder.build());

        request.setChaincodePath(chaincodePath);
        request.setChaincodeName(chaincodeName);
        request.setChaincodeVersion(chaincodeVersion);
        request.setChaincodeLanguage(TransactionRequest.Type.valueOf(language));
        request.setFcn(function);
        if (args != null) {
            request.setArgs(args);
        }

        if (endorsementPolicy != null) {
            request.setChaincodeEndorsementPolicy(endorsementPolicy);
        }

        if (request instanceof InstallProposalRequest) {
            InstallProposalRequest req = (InstallProposalRequest) request;
            if (chaincodeInputStream != null) {
                try {
                    req.setChaincodeInputStream(chaincodeInputStream);
                } catch (InvalidArgumentException e) {
                    throw new IllegalArgumentException(e);
                }
            }
            if (chaincodeSourceLocation != null) {
                try {
                    req.setChaincodeSourceLocation(chaincodeSourceLocation);
                } catch (InvalidArgumentException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        if (transientMap != null && !transientMap.isEmpty()) {
            try {
                if (request instanceof TransactionProposalRequest) {
                    TransactionProposalRequest req = (TransactionProposalRequest) request;
                    req.setTransientMap(transientMap);
                } else if (request instanceof QueryByChaincodeRequest) {
                    QueryByChaincodeRequest req = (QueryByChaincodeRequest) request;
                    req.setTransientMap(transientMap);
                } else if (request instanceof InstantiateProposalRequest) {
                    InstantiateProposalRequest req = (InstantiateProposalRequest) request;
                    req.setTransientMap(transientMap);
                } else if (request instanceof UpgradeProposalRequest) {
                    UpgradeProposalRequest req = (UpgradeProposalRequest) request;
                    req.setTransientMap(transientMap);
                }
            } catch (Exception e) {
            }
        }
    }
}
