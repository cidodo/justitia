package org.hyperledger.justitia.channel.service;

import org.hyperledger.justitia.channel.exception.ChannelServiceException;
import org.hyperledger.justitia.common.utils.file.FileUtils;
import org.hyperledger.justitia.common.face.service.channel.ChannelManageService;
import org.hyperledger.justitia.common.bean.channel.ChannelInfo;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.hyperledger.justitia.common.face.service.fabric.FabricToolsService;
import org.hyperledger.justitia.common.face.service.fabric.NetworkService;
import org.hyperledger.justitia.common.bean.channel.ChannelMember;
import org.hyperledger.justitia.common.face.service.identity.OrganizationService;
import org.hyperledger.justitia.common.face.service.identity.msp.MspService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.*;

@Service
public class ChannelManageServiceImpl implements ChannelManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelManageServiceImpl.class);
    private final ChannelService channelService;
    private final NetworkService networkService;
    private final FabricToolsService fabricToolsService;
    private final OrganizationService organizationService;
    private final MspService mspService;

    @Autowired
    public ChannelManageServiceImpl(ChannelService channelService, NetworkService networkService,
                                    FabricToolsService fabricToolsService, OrganizationService organizationService, MspService mspService) {
        this.channelService = channelService;
        this.networkService = networkService;
        this.fabricToolsService = fabricToolsService;
        this.organizationService = organizationService;
        this.mspService = mspService;
    }


    @Override
    public void createChannel(ChannelInfo channelInfo) {
        OrganizationInfo organizationInfo = organizationService.getOrganizationInfo();
        String memberName = organizationInfo.getName();
        String mspId = organizationInfo.getMspId();
        File mspDirectory = generateMsp();

        byte[] createChannelTx = fabricToolsService.generateCreateChannelTx(memberName, mspId, mspDirectory, channelId, consortium);
        byte[] signature = channelService.getChannelConfigurationSignature(createChannelTx);
        channelService.createChannel(channelId, createChannelTx, signature);

        if (null != peersId && !peersId.isEmpty()) {
            peerJoinChannel(channelId, peersId);
        }
    }

    private File generateMsp() {
        File tempDirectory = FileUtils.createTempDirectory();
        return mspService.getOrganizationMSP(tempDirectory);
    }

    @Override
    public void peerJoinChannel(String channelId, Collection<String> peersId) {
        if (null == peersId || peersId.isEmpty()) {
            throw new IllegalArgumentException("Peer参数为空，无法完成加入通道操作。");
        }
        ArrayList<String> failedPeers = new ArrayList<>();
        try {
            for (String peerId : peersId) {
                try {
                    channelService.peerJoinChannel(channelId, peerId);
                } catch (Throwable  e) {
                    LOGGER.error("PeerInfo {} failed to join channel {}.", peerId, channelId, e);
                    failedPeers.add(peerId);
                }
            }
        }finally {
            networkService.resetNetwork();
        }
        if (null != failedPeers && !failedPeers.isEmpty()) {
            String msg = String.format("节点 %s 加入通道%s失败。", Arrays.toString(failedPeers.toArray()), channelId);
//            String msg = String.format("Node %s failed to join channel %s.", Arrays.toString(failedPeers.toArray()), channelId);
            throw new ChannelServiceException(msg);
        }
    }

    @Override
    public List<ChannelInfo> getChannelsInfo() {
        List<ChannelInfo> channelsInfo = new ArrayList<>();
        Set<String> channelsId = networkService.getChannelsId();
        if (null != channelsId && !channelsId.isEmpty()) {
            for (String channelId : channelsId) {
                ChannelInfo channelInfo = getChannelInfo(channelId);
                if (null != channelInfo) {
                    channelsInfo.add(channelInfo);
                }
            }
        }
        return channelsInfo;
    }

    @Override
    public ChannelInfo getChannelInfo(String channelId) {
        if (null == channelId || channelId.isEmpty()) {
            return null;
        }
        ChannelInfo channelInfo = new ChannelInfo(channelId);
        Set<String> peersId = networkService.getPeersId(channelId);
        if (null != peersId) {
            for (String peerId : peersId) {
                channelInfo.addPeer(peerId);
            }
        }
        List<ChannelMember> organizations = networkService.getChannelMembers(channelId);
        if (null != organizations) {
            for (ChannelMember organization : organizations) {
                ChannelInfo.Member orgInfo = new ChannelInfo.Member(organization.getName(), organization.getMspId(), organization.getAnchorPeers());
                channelInfo.addMember(orgInfo);
            }
        }
        return channelInfo;
    }

    @Override
    public List<String> getChannelMspId(String channelId) {
        if (null == channelId || channelId.isEmpty()) {
            return null;
        }
        List<String> mspsId = new ArrayList<>();
        List<ChannelMember> organizations = networkService.getChannelMembers(channelId);
        if (null != organizations) {
            for (ChannelMember organization : organizations) {
                mspsId.add(organization.getMspId());
            }
        }
        return mspsId;
    }

    @Override
    public InputStream getMemberConfig(String memberId) {
        OrganizationInfo organizationInfo = organizationService.getOrganizationInfo();
        String memberName = organizationInfo.getName();
        String mspId = organizationInfo.getMspId();
        File mspDirectory = generateMsp();
        return fabricToolsService.generateMemberConfig(memberName, mspId, mspDirectory, memberId);
    }

    @Override
    public void addMember(String channelId,  ChannelMember member, String description) {
        memberManager.addMember(channelId, orgName, orgConfig, description);
    }

    @Override
    public void deleteMember(String channelId, String memberName, String description) {
        memberManager.deleteMember(channelId, memberName, description);
    }

    @Override
    public void addAnchorPeer() {
        //TODO 增加锚节点，参考fabric-service-java的UpdateChannelIT.java(266行左右)
    }

    @Override
    public void deleteAnchorPeer() {

    }
}
