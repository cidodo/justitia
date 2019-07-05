package org.hyperledger.justitia.channel.service;

import org.hyperledger.justitia.channel.service.beans.ChannelInfo;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;


public interface ChannelManageService {
    void createChannel(String channelId, String consortium, Collection<String> peersId);
    void peerJoinChannel(String channelId, Collection<String> peersId);
    List<ChannelInfo> getChannelsInfo();
    ChannelInfo getChannelInfo(String channelId);
    List<String> getChannelMspId(String channelId);
    InputStream getMemberConfig();
    void addMember(String channelId, String orgName, String orgConfig, String description);
    void deleteMember(String channelId, String orgName, String description);
    void addAnchorPeer();
    void deleteAnchorPeer();
}
