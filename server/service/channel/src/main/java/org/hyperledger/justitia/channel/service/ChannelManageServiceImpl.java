package org.hyperledger.justitia.channel.service;

import org.hyperledger.justitia.channel.exception.ChannelServiceException;
import org.hyperledger.justitia.channel.service.member.CMSCCRequestBean;
import org.hyperledger.justitia.channel.service.member.ChannelMemberService;
import org.hyperledger.justitia.common.bean.identity.Organization;
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
import java.io.IOException;
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
    private final ChannelMemberService channelMemberService;
    private final ChannelConfigProposalServiceImpl channelConfigProposalService;


    @Autowired
    public ChannelManageServiceImpl(ChannelService channelService, NetworkService networkService,
                                    FabricToolsService fabricToolsService, OrganizationService organizationService,
                                    MspService mspService, ChannelMemberService channelMemberService,
                                    ChannelConfigProposalServiceImpl channelConfigProposalService) {
        this.channelService = channelService;
        this.networkService = networkService;
        this.fabricToolsService = fabricToolsService;
        this.organizationService = organizationService;
        this.mspService = mspService;
        this.channelMemberService = channelMemberService;
        this.channelConfigProposalService = channelConfigProposalService;
    }


    @Override
    public void createChannel(ChannelInfo channelInfo) {
        String channelId = channelInfo.getChannelId();
        String consortium = channelInfo.getConsortium();

        Organization organization = organizationService.getOrganization();
        String memberName = organization.getName();
        String mspId = organization.getMspId();
        File mspDirectory = generateMsp();

        byte[] createChannelTx = fabricToolsService.generateCreateChannelTx(memberName, mspId, mspDirectory, channelId, consortium);
        byte[] signature;
        try {
            signature = channelService.getChannelConfigurationSignature(createChannelTx);
        } catch (Throwable e) {
            throw new ChannelServiceException(ChannelServiceException.CREATE_CHAIN_TRANSACTION_SIGN_ERROR, e);
        }
        try {
            channelService.createChannel(channelId, createChannelTx, signature);
        } catch (Throwable e) {
            throw new ChannelServiceException(ChannelServiceException.CREATE_TRANSACTION_SUBMIT_ERROR, e);
        }

        List<String> peersId = channelInfo.getPeers();
        if (null != peersId && !peersId.isEmpty()) {
            peerJoinChannel(channelId, peersId);
        }
    }

    private File generateMsp() {
        File tempDirectory;
        try {
            tempDirectory = FileUtils.createTempDirectory();
        } catch (IOException e) {
            throw new ChannelServiceException(ChannelServiceException.UNKNOWN_EXCEPTION, e);
        }
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
            //fixme 暂时手动触发，最好做节点发现，然后自动触发
            networkService.resetNetwork();
        }
        if (!failedPeers.isEmpty()) {
            throw new ChannelServiceException(ChannelServiceException.PEER_JOIN_ERROR, Arrays.toString(failedPeers.toArray()), channelId);
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
        List<ChannelMember> members = networkService.getChannelMembers(channelId);
        channelInfo.setMembers(members);
        return channelInfo;
    }

    @Override
    public List<String> getMembersMspId(String channelId) {
        if (null == channelId || channelId.isEmpty()) {
            return null;
        }
        List<String> membersMsp = new ArrayList<>();
        List<ChannelMember> organizations = networkService.getChannelMembers(channelId);
        if (null != organizations) {
            for (ChannelMember organization : organizations) {
                membersMsp.add(organization.getMspId());
            }
        }
        return membersMsp;
    }

    @Override
    public InputStream generateMemberConfig() {
        Organization organization = organizationService.getOrganization();
        String memberName = organization.getName();
        String mspId = organization.getMspId();
        File mspDirectory = generateMsp();
        return fabricToolsService.generateMemberConfig(memberName, mspId, mspDirectory);
    }

    @Override
    public void addMember(String channelId, ChannelMember member, String description) {
        CMSCCRequestBean proposalInfo = channelMemberService.addMember(channelId, member.getName(), member.getMemberConfig(), description);
        String proposalId = channelConfigProposalService.createSignProposal(proposalInfo);
        //todo DB
    }

    @Override
    public void deleteMember(String channelId, String memberName, String description) {
        CMSCCRequestBean proposalInfo = channelMemberService.deleteMember(channelId, memberName, description);
        String proposalId = channelConfigProposalService.createSignProposal(proposalInfo);
        //todo DB
    }

    @Override
    public void addAnchorPeer() {
        //TODO 增加锚节点，参考fabric-service-java的UpdateChannelIT.java(266行左右)
    }

    @Override
    public void deleteAnchorPeer() {

    }
}
