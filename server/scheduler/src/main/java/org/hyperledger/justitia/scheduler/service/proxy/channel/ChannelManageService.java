package org.hyperledger.justitia.scheduler.service.proxy.channel;

import org.hyperledger.justitia.common.bean.channel.ChannelInfo;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class ChannelManageService implements org.hyperledger.justitia.common.face.service.channel.ChannelManageService {
    @Override
    public void createChannel(String channelId, String consortium, Collection<String> peersId) {

    }

    @Override
    public void peerJoinChannel(String channelId, Collection<String> peersId) {

    }

    @Override
    public List<ChannelInfo> getChannelsInfo() {
        return null;
    }

    @Override
    public ChannelInfo getChannelInfo(String channelId) {
        return null;
    }

    @Override
    public List<String> getChannelMspId(String channelId) {
        return null;
    }

    @Override
    public InputStream getMemberConfig() {
        return null;
    }

    @Override
    public void addMember(String channelId, String orgName, String orgConfig, String description) {

    }

    @Override
    public void deleteMember(String channelId, String orgName, String description) {

    }

    @Override
    public void addAnchorPeer() {

    }

    @Override
    public void deleteAnchorPeer() {

    }
}
