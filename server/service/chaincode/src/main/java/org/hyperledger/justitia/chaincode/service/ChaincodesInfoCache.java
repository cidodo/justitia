package org.hyperledger.justitia.chaincode.service;

import org.hyperledger.justitia.common.bean.chaincode.ChaincodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChaincodesInfoCache {
    private final static Map<String, List<ChaincodeInfo>> chaincodesInfoMap = new ConcurrentHashMap<>();

    static void setChaincodesInfo(String peerId, List<ChaincodeInfo> chaincodesInfo) {
        ChaincodesInfoCache.chaincodesInfoMap.put(peerId, chaincodesInfo);
    }

    static void updateInstantiatedChaincodes(String peerId, String channelId, List<ChaincodeInfo> chaincodesInfo) {
        List<ChaincodeInfo> chaincodesInfoByChannel = getChaincodesInfo(peerId, channelId);
        for (ChaincodeInfo chaincodeInfo : chaincodesInfo) {
            for (ChaincodeInfo chaincodeInfoByChannel : chaincodesInfoByChannel) {
                if (chaincodeInfo.getName().equals(chaincodeInfoByChannel.getName())
                        && chaincodeInfo.getVersion().equals(chaincodeInfoByChannel.getVersion())) {
                    chaincodeInfoByChannel.addInstantiatedInfo(channelId, chaincodeInfo.getInstantiatedInfo(channelId));
                }
            }
        }
    }

    private static List<ChaincodeInfo> getChaincodesInfo(String peerId) {
        if (chaincodesInfoMap.containsKey(peerId)) {
            return chaincodesInfoMap.get(peerId);
        } else {
            return new ArrayList<>();
        }
    }

    private static List<ChaincodeInfo> getChaincodesInfo(String peerId, String channelId) {
        List<ChaincodeInfo> chaincodesByChannel = new ArrayList<>();
        List<ChaincodeInfo> chaincodesInfo = getChaincodesInfo(peerId);
        if (null != chaincodesInfo && !chaincodesInfo.isEmpty()) {
            for (ChaincodeInfo chaincodeInfo : chaincodesInfo) {
                if (null != chaincodeInfo.getInstantiatedInfo(channelId)) {
                    chaincodesByChannel.add(chaincodeInfo);
                }
            }
        }
        return chaincodesByChannel;
    }
}
