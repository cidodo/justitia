package org.hyperledger.justitia.farbic.data;

import org.hyperledger.justitia.service.face.fabric.bean.ChannelMember;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkConfig {
    private static final Map<String, Set<String>> peerRefChannel = new ConcurrentHashMap<>();
    private static final Map<String, List<ChannelMember>> channelRefOrg = new ConcurrentHashMap<>();

    static void setPeerRefChannel(Map<String, Set<String>> peerRefChannel) {
        synchronized (NetworkConfig.peerRefChannel) {
            NetworkConfig.peerRefChannel.clear();
            NetworkConfig.peerRefChannel.putAll(peerRefChannel);
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

    static Set<String> getPeersId() {
        return peerRefChannel.keySet();
    }

    static Set<String> getChannelsIdByPeer(String peerId) {
        return peerRefChannel.get(peerId);
    }

    static Set<String> getChannelsId() {
        Set<String> allChannelsId = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : peerRefChannel.entrySet()) {
            Set<String> channelsId = entry.getValue();
            allChannelsId.addAll(channelsId);
        }
        return allChannelsId;
    }

    static void setChannelRefOrg(Map<String, List<ChannelMember>> channelRefOrg) {
        synchronized (NetworkConfig.channelRefOrg) {
            NetworkConfig.channelRefOrg.clear();
            NetworkConfig.channelRefOrg.putAll(channelRefOrg);
        }
    }

    static Map<String, List<ChannelMember>> getChannelRefOrg() {
        return channelRefOrg;
    }

    static List<ChannelMember> getOrganizations(String channelId) {
        return channelRefOrg.get(channelId);
    }

    static Set<String> getChannelsIdByOrganization(String organizationName) {
        if (null == organizationName) {
            return null;
        }
        HashSet<String> channelsId = new HashSet<>();
        synchronized (channelRefOrg) {
            for (Map.Entry<String, List<ChannelMember>> entry : channelRefOrg.entrySet()) {
                String channelId = entry.getKey();
                List<ChannelMember> organizationsInfo = entry.getValue();
                if (null != organizationsInfo) {
                    for (ChannelMember organizationInfo : organizationsInfo) {
                        if (organizationName.equals(organizationInfo.getName())) {
                            channelsId.add(channelId);
                        }
                    }
                }
            }
        }
        return channelsId;
    }
}
