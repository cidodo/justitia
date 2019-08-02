package org.hyperledger.justitia.common.bean.chaincode;

import lombok.Data;
import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ChaincodeInfo {
    private String name;
    private String version;
    private String language;
    private String path;
    private String input;
    private String id;

    private Map<String,InstantiatedInfo> instantiatedInfoMap = new ConcurrentHashMap<>();


    public void addInstantiatedInfo(String channelId, InstantiatedInfo instantiatedInfo) {
        instantiatedInfoMap.put(channelId, instantiatedInfo);
    }

    public InstantiatedInfo getInstantiatedInfo(String channelId) {
        return instantiatedInfoMap.get(channelId);
    }

    @Data
    public static class InstantiatedInfo {
        private ChaincodeEndorsementPolicy endorsementPolicy;
        private String escc;
        private String vscc;
    }
}
