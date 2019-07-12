package org.hyperledger.justitia.farbic.data;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.protos.msp.MspConfig;
import org.hyperledger.fabric.protos.peer.Configuration;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.justitia.common.face.modules.fabric.bean.ChannelMember;
import org.hyperledger.justitia.farbic.exception.HFClientContextException;
import org.hyperledger.justitia.farbic.sdk.ChannelManager;
import org.hyperledger.justitia.common.face.modules.identity.beans.PeerInfo;
import org.hyperledger.justitia.common.face.modules.identity.read.NodeReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SyncData4Chain implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncData4Chain.class);
    private final NodeReader nodeReader;
    private final ChannelManager channelManager;

    private static Boolean syncing = false;
    private static final int SYNC_PERIOD = 5;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Autowired
    public SyncData4Chain(NodeReader nodeReader, ChannelManager channelManager) {
        this.nodeReader = nodeReader;
        this.channelManager = channelManager;
    }

    @Override
    public void afterPropertiesSet() {
        begin();
        executorService.scheduleAtFixedRate(() -> {
            if (syncing) {
                syncData();
            }
        }, 0, SYNC_PERIOD, TimeUnit.MINUTES);
    }


    public void begin() {
        syncing = true;
    }

    public void stop() {
        syncing = false;
    }

    public void syncData() {
        try {
            Map<String, Set<String>> peerRefChannel = syncPeerRefChannel();
            PeerRefChannel.setPeerRefChannel(peerRefChannel);
            Map<String, List<ChannelMember>> channelRefOrg = syncChannelRefOrg();
            ChannelRefOrganization.setChannelRefOrg(channelRefOrg);
        } catch (Exception e) {
            LOGGER.warn("Synchronize data from chain failed.", e);
        }
    }


    private Map<String, Set<String>> syncPeerRefChannel() {
        Map<String, Set<String>> peerRefChannel = new ConcurrentHashMap<>();
        List<PeerInfo> peersInfo = nodeReader.getPeersInfo();
        if (null != peersInfo && !peersInfo.isEmpty()) {
            for (PeerInfo peerInfo : peersInfo) {
                String peerId = peerInfo.getId();
                Set<String> channelsId;
                try {
                    channelsId = channelManager.queryChannels(peerId);
                } catch (InvalidArgumentException | ProposalException e) {
                    LOGGER.warn("Get channel list failed.", e);
                    continue;
                } catch (HFClientContextException e) {
                    LOGGER.debug("HFClient context exception.", e);
                    continue;
                }
                peerRefChannel.put(peerId, channelsId);
            }
        }
        return peerRefChannel;
    }

    private Map<String, List<ChannelMember>> syncChannelRefOrg() {
        Map<String, List<ChannelMember>> channelRefOrg = new ConcurrentHashMap<>();
        Set<String> channelsId = PeerRefChannel.getAllChannelsId();
        if (null != channelsId && !channelsId.isEmpty()) {
            for (String channelId : channelsId) {
                Configtx.Config config;
                try {
                    byte[] channelConfigBytes = channelManager.getChannelConfigurationBytes(channelId);
                    config = Configtx.Config.parseFrom(channelConfigBytes);
                } catch (TransactionException | InvalidProtocolBufferException | InvalidArgumentException e) {
                    LOGGER.warn("get channel{} config failed.", channelId, e);
                    continue;
                } catch (HFClientContextException e) {
                    LOGGER.debug("HFClient context exception.", e);
                    continue;
                }

                List<ChannelMember> organizationsInfo = new ArrayList<>();
                Configtx.ConfigGroup application = config.getChannelGroup().getGroupsMap().get("Application");
                Map<String, Configtx.ConfigGroup> groupsMap = application.getGroupsMap();
                for (Map.Entry<String, Configtx.ConfigGroup> group : groupsMap.entrySet()) {
                    //organization name
                    String organizationName = group.getKey();
                    String mspId = null;
                    List<String> anchorPeersStr = new ArrayList<>();
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
                    organizationsInfo.add(new ChannelMember(organizationName, mspId,  anchorPeersStr));
                }
                channelRefOrg.put(channelId, organizationsInfo);
            }
        }
        return channelRefOrg;
    }
}
