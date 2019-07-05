package org.hyperledger.justitia.farbic.data;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelRefOrganization {
    private static final Map<String, List<OrganizationInfo>> channelRefOrg = new ConcurrentHashMap<>();

    static void setChannelRefOrg(Map<String, List<OrganizationInfo>> channelRefOrg) {
        synchronized (ChannelRefOrganization.channelRefOrg) {
            ChannelRefOrganization.channelRefOrg.clear();
            ChannelRefOrganization.channelRefOrg.putAll(channelRefOrg);
        }
    }

    static Map<String, List<OrganizationInfo>> getChannelRefOrg() {
        return channelRefOrg;
    }

    static List<OrganizationInfo> getOrganizations(String channelId) {
        return channelRefOrg.get(channelId);
    }

    static Set<String> getChannelsId(String organizationName) {
        if (null == organizationName) {
             return null;
        }
        HashSet<String> channelsId = new HashSet<>();
        synchronized (channelRefOrg) {
            for (Map.Entry<String, List<OrganizationInfo>> entry : channelRefOrg.entrySet()) {
                String channelId = entry.getKey();
                List<OrganizationInfo> organizationsInfo = entry.getValue();
                if (null != organizationsInfo) {
                    for (OrganizationInfo organizationInfo : organizationsInfo) {
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
