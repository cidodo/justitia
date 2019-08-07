package org.hyperledger.justitia.farbic.data;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.protos.msp.MspConfig;
import org.hyperledger.fabric.protos.peer.Configuration;
import org.hyperledger.justitia.common.bean.node.PeerInfo;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.hyperledger.justitia.common.bean.channel.ChannelMember;
import org.hyperledger.justitia.common.face.service.identity.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SyncData4Chain implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncData4Chain.class);
    private final ChannelService channelService;
    private final NodeService nodeService;

    private static Boolean pause = false;
    private static final int SYNC_PERIOD = 5;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Autowired
    public SyncData4Chain(ChannelService channelService, NodeService nodeService) {
        this.channelService = channelService;
        this.nodeService = nodeService;
    }

    @Override
    public void afterPropertiesSet() {
        pause = false;
        executorService.scheduleAtFixedRate(() -> {
            if (!pause) {
                syncData();
            }
        }, 0, SYNC_PERIOD, TimeUnit.MINUTES);
    }


    public void contuiteSync() {
        pause = false;
    }

    public void pauseSync() {
        pause = true;
    }

    public void syncData() {
        try {
            Map<String, Set<String>> peerRefChannel = syncPeerRefChannel();
            NetworkConfig.setPeerRefChannel(peerRefChannel);
            Map<String, List<ChannelMember>> channelRefOrg = syncChannelRefOrg();
            NetworkConfig.setChannelRefOrg(channelRefOrg);
        } catch (Throwable  e) {
            LOGGER.warn("Synchronize data from chain failed.", e);
        }
    }


    private Map<String, Set<String>> syncPeerRefChannel() {
        Map<String, Set<String>> peerRefChannel = new ConcurrentHashMap<>();
        Collection<PeerInfo> peersInfo = nodeService.getPeersInfo();
        if (null != peersInfo && !peersInfo.isEmpty()) {
            for (PeerInfo peerInfo : peersInfo) {
                String peerId = peerInfo.getId();
                Set<String> channelsId;
                try {
                    channelsId = channelService.queryChannels(peerId);
                } catch (Throwable  e) {
                    LOGGER.warn("Get channel list failed.", e);
                    continue;
                }
                peerRefChannel.put(peerId, channelsId);
            }
        }
        return peerRefChannel;
    }

    private Map<String, List<ChannelMember>> syncChannelRefOrg() {
        Map<String, List<ChannelMember>> channelRefOrg = new ConcurrentHashMap<>();
        Set<String> channelsId = NetworkConfig.getChannelsId();
        if (null != channelsId && !channelsId.isEmpty()) {
            for (String channelId : channelsId) {
                Configtx.Config config;
                try {
                    byte[] channelConfigBytes = channelService.getChannelConfigurationBytes(channelId);
                    config = Configtx.Config.parseFrom(channelConfigBytes);
                } catch (Throwable  e) {
                    LOGGER.warn("get channel{} config failed.", channelId, e);
                    continue;
                }

                List<ChannelMember> members = new ArrayList<>();
                Configtx.ConfigGroup application = config.getChannelGroup().getGroupsMap().get("Application");
                Map<String, Configtx.ConfigGroup> groupsMap = application.getGroupsMap();
                for (Map.Entry<String, Configtx.ConfigGroup> group : groupsMap.entrySet()) {
                    //organization name
                    String organizationName = group.getKey();
                    String mspId = null;
                    Set<String> anchorPeersStr = new HashSet<>();
                    Configtx.ConfigGroup configGroup = group.getValue();
                    Map<String, Configtx.ConfigValue> valuesMap = configGroup.getValuesMap();
                    //AnchorPeers
                    if (valuesMap.containsKey("AnchorPeers")) {
                        Configtx.ConfigValue anchorPeersConfig = valuesMap.get("AnchorPeers");
                        List<Configuration.AnchorPeer> anchorPeersList;
                        try {
                            Configuration.AnchorPeers anchorPeers = Configuration.AnchorPeers.parseFrom(anchorPeersConfig.getValue());
                            anchorPeersList = anchorPeers.getAnchorPeersList();
                        } catch (InvalidProtocolBufferException e) {
                            LOGGER.warn("anchor peer info parse failed." + e.getMessage());
                            break;
                        }

                        if (anchorPeersList != null && !anchorPeersList.isEmpty()) {
                            for (Configuration.AnchorPeer anchorPeer : anchorPeersList) {
                                anchorPeersStr.add(anchorPeer.getHost() + ":" + anchorPeer.getPort());
                            }
                        }
                    }
                    //MSPID
                    ByteString value = configGroup.getValuesMap().get("MSP").getValue();
                    try {
                        ByteString mspConfig = MspConfig.MSPConfig.parseFrom(value).getConfig();
                        mspId = MspConfig.FabricMSPConfig.parseFrom(mspConfig).getName();
                    } catch (InvalidProtocolBufferException e) {
                        LOGGER.warn("MSP configuration parsing failed.", e);
                    }
                    ChannelMember member = new ChannelMember(organizationName);
                    member.setMspId(mspId);
                    member.setAnchorPeers(anchorPeersStr);
                    members.add(member);
                }
                channelRefOrg.put(channelId, members);
            }
        }
        return channelRefOrg;
    }
}
