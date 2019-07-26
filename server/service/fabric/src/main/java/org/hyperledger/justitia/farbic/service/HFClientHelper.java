package org.hyperledger.justitia.farbic.service;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.justitia.farbic.exception.FabricServiceException;
import org.hyperledger.justitia.service.face.fabric.NetworkService;
import org.hyperledger.justitia.service.face.identity.NodeService;
import org.hyperledger.justitia.service.face.identity.UserService;
import org.hyperledger.justitia.service.face.identity.bean.NodeInfo;
import org.hyperledger.justitia.service.face.identity.bean.OrdererInfo;
import org.hyperledger.justitia.service.face.identity.bean.PeerInfo;
import org.hyperledger.justitia.service.face.identity.bean.FabricUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.hyperledger.justitia.farbic.exception.FabricServiceException.*;


@Service
public class HFClientHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HFClientHelper.class);
    private static final String FABRIC_SDK_CONFIG_FILE = "resources/fabric/config/config.properties";

    static {
        System.setProperty(Config.ORG_HYPERLEDGER_FABRIC_SDK_CONFIGURATION, FABRIC_SDK_CONFIG_FILE);
    }

    private final UserService userService;
    private final NodeService nodeService;
    private final NetworkService networkService;

    @Autowired
    public HFClientHelper(UserService userService, NodeService nodeService, NetworkService networkService) {
        this.userService = userService;
        this.nodeService = nodeService;
        this.networkService = networkService;
    }

    public User getAdminUser() {
        FabricUserInfo adminUserInfo = userService.getAdminUser();
        if (null == adminUserInfo) {
            throw new FabricServiceException(NO_ADMIN_USER);
        }
        try {
            return adminUserInfo.getFabricUser();
        } catch (IOException e) {
            throw new FabricServiceException(FABRIC_USER_CRYPTO_PARSE_ERROR, e, adminUserInfo.getId());
        }
    }

    public HFClient getHFClientWithAdmin() {
        FabricUserInfo adminUserInfo = userService.getAdminUser();
        if (null == adminUserInfo) {
            throw new FabricServiceException(NO_ADMIN_USER);
        }
        return createHFClient(adminUserInfo);
    }

    public HFClient getHFClient() {
        FabricUserInfo randomUserInfo = userService.getRandomUser();
        if (null == randomUserInfo) {
            throw new FabricServiceException(NO_USERS);
        }
        return createHFClient(randomUserInfo);
    }

    public HFClient getHFClient(String userId) {
        FabricUserInfo userInfo = userService.getUser(userId);
        if (null == userInfo) {
            throw new FabricServiceException(NOT_FOUND_USER_BY_ID, userId);
        }
        return createHFClient(userInfo);
    }

    private HFClient createHFClient(FabricUserInfo userInfo) {
        HFClient client = HFClient.createNewInstance();
        try {
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        } catch (Throwable  e) {
            throw new FabricServiceException(CRYPTO_SUITE_ERROR, e);
        }
        User user;
        try {
            user = userInfo.getFabricUser();
        } catch (IOException e) {
            throw new FabricServiceException(FABRIC_USER_CRYPTO_PARSE_ERROR, e, userInfo.getId());
        }
        try {
            client.setUserContext(user);
        } catch (InvalidArgumentException e) {
            throw new FabricServiceException(USER_CONTEXT_ERROR, e);
        }
        return client;
    }

    public Peer createPeer(HFClient client, String peerId) {
        PeerInfo peerInfo = nodeService.getPeerInfo(peerId);
        if (null == peerInfo) {
            throw new FabricServiceException(NOT_FOUND_PEER_BY_ID, peerId);
        }
        try {
            return createNode(client, peerInfo, Peer.class);
        } catch (InvalidArgumentException e) {
            throw new FabricServiceException(CREATE_PEER_ERROR, peerId);
        }
    }

    public Peer createPeerByChannel(HFClient client, String channelId) {
        Set<String> peersId = networkService.getPeersId(channelId);
        if (null == peersId || peersId.isEmpty()) {
            return null;
        }
        ArrayList<String> peersIdArray = new ArrayList<>(peersId);
        Collections.shuffle(peersIdArray);
        return createPeer(client, peersIdArray.get(0));
    }

    public Set<Peer> createPeersByChannel(HFClient client, String channelId) {
        Set<String> peersId = networkService.getPeersId(channelId);
        Set<Peer> peers = createPeers(client, peersId);
        if (null == peers || peers.isEmpty()) {
            throw new FabricServiceException(NOT_FOUND_PEER_BY_CHANNEL, channelId);
        }
        return peers;
    }

    public Set<Peer> createPeers(HFClient client, Collection<String> peersId) {
        if (null == peersId || peersId.isEmpty()) {
            return null;
        }
        Set<Peer> peers = new HashSet<>();
        for (String peerId : peersId) {
            try {
                peers.add(createPeer(client, peerId));
            } catch (Throwable  e) {
                LOGGER.warn("Create peer object failed.", e);
            }
        }
        return peers;
    }

    public Orderer createOrderer(HFClient client) {
        OrdererInfo ordererInfo = nodeService.getOrdererInfo();
        if (null == ordererInfo) {
            throw new FabricServiceException(NO_ORDERERS);
        }
        try {
            return createNode(client, ordererInfo, Orderer.class);
        } catch (InvalidArgumentException e) {
            throw new FabricServiceException(CREATE_ORDERER_ERROR, ordererInfo.getId());
        }
    }

    public Orderer createOrderer(HFClient client, String ordererId) {
        OrdererInfo ordererInfo = nodeService.getOrdererInfo(ordererId);
        if (null == ordererInfo) {
            throw new FabricServiceException(NOT_FOUND_ORDERER_BY_ID, ordererId);
        }
        try {
            return createNode(client, ordererInfo, Orderer.class);
        } catch (InvalidArgumentException e) {
            throw new FabricServiceException(CREATE_ORDERER_ERROR, ordererInfo.getId());
        }
    }

    Set<Orderer> createOrderers(HFClient client) {
        List<OrdererInfo> orderersInfo = nodeService.getOrderersInfo();
        if (null == orderersInfo || orderersInfo.isEmpty()) {
            throw new FabricServiceException(NO_ORDERERS);
        }
        Set<Orderer> orderers = new HashSet<>();
        for (OrdererInfo ordererInfo : orderersInfo) {
            try {
                orderers.add(createNode(client, ordererInfo, Orderer.class));
            } catch (Throwable  e) {
                LOGGER.warn("Create orderer object failed.", e);
            }
        }
        return orderers;
    }

    public Set<Orderer> createOrderers(HFClient client, Collection<String> orderersId) {
        if (null == orderersId) {
            return null;
        }
        Set<Orderer> orderers = new HashSet<>();
        for (String ordererId : orderersId) {
            try {
                orderers.add(createOrderer(client, ordererId));
            } catch (Throwable  e) {
                LOGGER.warn("Create orderer object failed.", e);
            }
        }
        return orderers;
    }

    @SuppressWarnings("unchecked")
    private <T> T createNode(HFClient client, NodeInfo nodeInfo, Class<T> nodeType) throws InvalidArgumentException {
        if (nodeInfo == null) {
            return null;
        }

        if (Peer.class == nodeType) {
            return (T) client.newPeer(nodeInfo.getId(), nodeInfo.getUrl(), nodeInfo.getProperties());
        } else if (Orderer.class == nodeType) {
            return (T) client.newOrderer(nodeInfo.getId(), nodeInfo.getUrl(), nodeInfo.getProperties());
        } else {
            return null;
        }
    }


    public Channel createChannel(String channelId) {
        HFClient client = getHFClient();
        return createChannel(client, channelId);
    }

    public Channel createChannelWithAdmin(String channelId) {
        HFClient client = getHFClientWithAdmin();
        return createChannel(client, channelId);
    }

    public Channel createChannel(String channelId, String userId) {
        HFClient client = getHFClient(userId);
        return createChannel(client, channelId);
    }

    public Channel createChannel(HFClient client, String channelId) {
        Channel channel;
        try {
            channel = client.newChannel(channelId);
        } catch (InvalidArgumentException e) {
            throw new FabricServiceException(CREATE_CHANNEL_ERROR, e, channelId);
        }

        Set<Peer> peers = createPeersByChannel(client, channelId);
        if (null != peers) {
            for (Peer peer : peers) {
                try {
                    channel.addPeer(peer);
                } catch (InvalidArgumentException e) {
                    LOGGER.warn("Channel add peer {} failed.", peer.getName(), e);
                }
            }
        }

        Orderer orderer = createOrderer(client);
        try {
            channel.addOrderer(orderer);
        } catch (InvalidArgumentException e) {
            LOGGER.warn("Channel add orderer {} failed.", orderer.getName());
        }

        try {
            channel.initialize();
        } catch (InvalidArgumentException | TransactionException e) {
            throw new FabricServiceException(CHANNEL_INITIALIZE_ERROR, e, channelId);
        }
        return channel;
    }
}
