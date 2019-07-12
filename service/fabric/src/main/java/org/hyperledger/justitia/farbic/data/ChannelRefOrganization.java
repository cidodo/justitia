package org.hyperledger.justitia.farbic.data;

import org.hyperledger.justitia.common.face.modules.fabric.bean.ChannelMember;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelRefOrganization {
    private static final Map<String, List<ChannelMember>> channelRefOrg = new ConcurrentHashMap<>();

    static void setChannelRefOrg(Map<String, List<ChannelMember>> channelRefOrg) {
        synchronized (ChannelRefOrganization.channelRefOrg) {
            ChannelRefOrganization.channelRefOrg.clear();
            ChannelRefOrganization.channelRefOrg.putAll(channelRefOrg);
        }
    }

    static Map<String, List<ChannelMember>> getChannelRefOrg() {
        return channelRefOrg;
    }

    static List<ChannelMember> getOrganizations(String channelId) {
        return channelRefOrg.get(channelId);
    }

    static Set<String> getChannelsId(String organizationName) {
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
