package org.hyperledger.justitia.farbic.data;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PeerRefChannel {
    private static final Map<String, Set<String>> peerRefChannel = new ConcurrentHashMap<>();

    static void setPeerRefChannel(Map<String, Set<String>> peerRefChannel) {
        synchronized (PeerRefChannel.peerRefChannel) {
            PeerRefChannel.peerRefChannel.clear();
            PeerRefChannel.peerRefChannel.putAll(peerRefChannel);
        }
    }

    static Set<String> getPeersId(String channelId) {
        if (null == channelId || channelId.isEmpty()) {
            return null;
        }
        Set<String> peersId = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : peerRefChannel.entrySet()) {
            String peerId = entry.getKey();
            Set<String> channelsId = entry.getValue();
            for (String channel : channelsId) {
                if (channelId.equals(channel)) {
                    peersId.add(peerId);
                    break;
                }
            }
        }
        return peersId;
    }

    static Set<String> getChannelsId(String peerId) {
        return peerRefChannel.get(peerId);
    }

    static Set<String> getAllChannelsId() {
        Set<String> allChannelsId = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : peerRefChannel.entrySet()) {
            Set<String> channelsId = entry.getValue();
            allChannelsId.addAll(channelsId);
        }
        return allChannelsId;
    }
}
