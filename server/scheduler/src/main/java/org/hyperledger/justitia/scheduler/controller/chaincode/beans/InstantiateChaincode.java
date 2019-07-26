package org.hyperledger.justitia.scheduler.controller.chaincode.beans;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class InstantiateChaincode {
    @NotEmpty
    private String chaincodeName;
    private String function;
    private ArrayList<String> args;
    private String channelName;
    private String version;
    private Object policy;
    private ArrayList<String> peersName;

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
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

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object getPolicy() {
        return policy;
    }

    public void setPolicy(Object policy) {
        this.policy = policy;
    }

    public ArrayList<String> getPeersName() {
        return peersName;
    }

    public void setPeersName(ArrayList<String> peersName) {
        this.peersName = peersName;
    }

    public static class Policy{
        private int policyNum;
        private List<Policy> policy;

        public int getPolicyNum() {
            return policyNum;
        }

        public void setPolicyNum(int policyNum) {
            this.policyNum = policyNum;
        }

        public List<Policy> getPolicy() {
            return policy;
        }

        public void setPolicy(List<Policy> policy) {
            this.policy = policy;
        }
    }
}
