package org.hyperledger.justitia.farbic.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ChainDataService {
    Set<String> getPeersId(String channelId);
    Set<String> getAllChannelsId();
    Set<String> getChannelsIdByPeer(String peerId);
    Map<String, List<OrganizationInfo>> getChannelRefOrg();
    List<OrganizationInfo> getOrganizations(String channelId);
    Set<String> getChannelsIdByOrg(String organizationName);
}
