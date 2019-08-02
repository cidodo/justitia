package org.hyperledger.justitia.common.face.service.channel;

import org.hyperledger.justitia.common.bean.channel.ChannelInfo;
import org.hyperledger.justitia.common.bean.channel.ChannelMember;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;


public interface ChannelManageService {
    void createChannel(ChannelInfo channelInfo);
    void peerJoinChannel(String channelId, Collection<String> peersId);
    List<ChannelInfo> getChannelsInfo();
    ChannelInfo getChannelInfo(String channelId);
    List<String> getChannelMspId(String channelId);
    InputStream getMemberConfig(String memberId);
    void addMember(String channelId, ChannelMember member, String description);
    void deleteMember(String channelId, String memberName, String description);
    void addAnchorPeer();
    void deleteAnchorPeer();
}
