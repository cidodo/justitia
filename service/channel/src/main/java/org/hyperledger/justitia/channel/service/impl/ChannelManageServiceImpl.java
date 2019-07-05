package org.hyperledger.justitia.channel.service.impl;

import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.justitia.channel.exception.ChannelManageException;
import org.hyperledger.justitia.channel.service.ChannelManageService;
import org.hyperledger.justitia.channel.service.beans.ChannelInfo;
import org.hyperledger.justitia.channel.service.member.MemberManager;
import org.hyperledger.justitia.farbic.data.ChainDataService;
import org.hyperledger.justitia.farbic.data.OrganizationInfo;
import org.hyperledger.justitia.farbic.data.SyncData4Chain;
import org.hyperledger.justitia.farbic.exception.FabricToolsException;
import org.hyperledger.justitia.farbic.exception.HFClientContextException;
import org.hyperledger.justitia.farbic.sdk.ChannelManager;
import org.hyperledger.justitia.farbic.tools.ConfigTxGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class ChannelManageServiceImpl implements ChannelManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelManageServiceImpl.class);

    private final ConfigTxGen configtxgen;
    private final ChannelManager channelManager;
    private final MemberManager memberManager;
    private final ChainDataService chainDataService;
    private final SyncData4Chain syncData4Chain;

    @Autowired
    public ChannelManageServiceImpl(ConfigTxGen configtxgen, ChannelManager channelManager, MemberManager memberManager, ChainDataService chainDataService, SyncData4Chain syncData4Chain) {
        this.configtxgen = configtxgen;
        this.channelManager = channelManager;
        this.memberManager = memberManager;
        this.chainDataService = chainDataService;
        this.syncData4Chain = syncData4Chain;
    }

    @Override
    public void createChannel(String channelId, String consortium, Collection<String> peersId) {
        byte[] createChannelTx;
        try {
            createChannelTx = configtxgen.createChannelTx(channelId, consortium);
        } catch (FabricToolsException e) {
            throw new ChannelManageException("生成创建通道交易失败。", e);
//            throw new ChannelManageException("Generate create channel transaction failed.", e);
        }

        byte[] signature;
        try {
            signature = channelManager.getChannelConfigurationSignature(createChannelTx);
        } catch (HFClientContextException | InvalidArgumentException e) {
            throw new ChannelManageException("创建通道交易签名失败。", e);
//            throw new ChannelManageException("Create channel transaction signature failed.", e);
        }

        try {
            channelManager.createChannel(channelId, createChannelTx, signature);
        } catch (InvalidArgumentException | HFClientContextException | TransactionException e) {
            throw new ChannelManageException("通道创建失败。", e);
//            throw new ChannelManageException("Create channel failed.", e);
        }

        if (null != peersId && !peersId.isEmpty()) {
            peerJoinChannel(channelId, peersId);
        }
    }

    @Override
    public void peerJoinChannel(String channelId, Collection<String> peersId) {
        if (null == peersId || peersId.isEmpty()) {
            throw new ChannelManageException("Peer参数为空，无法完成加入通道操作。");
//            throw new ChannelManageException("Peers is empty, no peer will be added to the channel.");
        }
        ArrayList<String> failedPeers = new ArrayList<>();
        try {
            for (String peerId : peersId) {
                try {
                    channelManager.peerJoinChannel(channelId, peerId);
                } catch (Exception e) {
                    LOGGER.error("Peer {} failed to join channel {}.", peerId, channelId, e);
                    failedPeers.add(peerId);
                }
            }
        }finally {
            //FIXME 这个写法太粗暴
            syncData4Chain.syncData();
        }
        if (null != failedPeers && !failedPeers.isEmpty()) {
            String msg = String.format("节点 %s 加入通道%s失败。", Arrays.toString(failedPeers.toArray()), channelId);
//            String msg = String.format("Node %s failed to join channel %s.", Arrays.toString(failedPeers.toArray()), channelId);
            throw new ChannelManageException(msg);
        }
    }

    @Override
    public List<ChannelInfo> getChannelsInfo() {
        List<ChannelInfo> channelsInfo = new ArrayList<>();
        Set<String> channelsId = chainDataService.getAllChannelsId();
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
        Set<String> peersId = chainDataService.getPeersId(channelId);
        if (null != peersId) {
            for (String peerId : peersId) {
                channelInfo.addPeer(peerId);
            }
        }
        List<OrganizationInfo> organizations = chainDataService.getOrganizations(channelId);
        if (null != organizations) {
            for (OrganizationInfo organization : organizations) {
                ChannelInfo.OrgInfo orgInfo = new ChannelInfo.OrgInfo(organization.getName(), organization.getMspId(), organization.getAnchorPeers());
                channelInfo.addOrg(orgInfo);
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
        List<OrganizationInfo> organizations = chainDataService.getOrganizations(channelId);
        if (null != organizations) {
            for (OrganizationInfo organization : organizations) {
                mspsId.add(organization.getMspId());
            }
        }
        return mspsId;
    }

    @Override
    public InputStream getMemberConfig() {
        try {
            return configtxgen.createOrgConfig();
        } catch (FabricToolsException e) {
            String msg = "生成组织配置失败。";
//            String msg = "Organization configuration generation failed.";
            throw new ChannelManageException(msg, e);
        }
    }

    @Override
    public void addMember(String channelId, String orgName, String orgConfig, String description) {
        memberManager.addOrganization(channelId, orgName, orgConfig, description);
        //FIXME 这个写法太粗暴
        syncData4Chain.syncData();
    }

    @Override
    public void deleteMember(String channelId, String orgName, String description) {
        memberManager.deleteOrganization(channelId, orgName, description);
        //FIXME 这个写法太粗暴
        syncData4Chain.syncData();
    }

    @Override
    public void addAnchorPeer() {
        //TODO 增加锚节点，参考fabric-sdk-java的UpdateChannelIT.java(266行左右)
    }

    @Override
    public void deleteAnchorPeer() {

    }
}
