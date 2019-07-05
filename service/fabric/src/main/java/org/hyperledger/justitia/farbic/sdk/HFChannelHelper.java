package org.hyperledger.justitia.farbic.sdk;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.justitia.common.utils.StringUtils;
import org.hyperledger.justitia.farbic.data.ChainDataService;
import org.hyperledger.justitia.farbic.exception.HFClientContextException;
import org.hyperledger.justitia.farbic.data.PeerRefChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class HFChannelHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HFChannelHelper.class);
    private final HFClientHelper clientHelper;
    private final ChainDataService chainDataService;

    @Autowired
    public HFChannelHelper(HFClientHelper clientHelper, ChainDataService chainDataService) {
        this.clientHelper = clientHelper;
        this.chainDataService = chainDataService;
    }

    public Channel createChannelNonPeer(String channelId,  String ordererId) throws TransactionException, InvalidArgumentException, HFClientContextException {
        return createChannel(channelId, ordererId, null);
    }

    public Channel createChannelNonPeer(String channelId,  List<String> orderersId) throws TransactionException, InvalidArgumentException, HFClientContextException {
        return createChannel(channelId, orderersId, null);
    }

    public Channel createChannelNonOrderer(String channelId,  String peerId) throws TransactionException, InvalidArgumentException, HFClientContextException {
        return createChannel(channelId, null, peerId);
    }

    public Channel createChannelNonOrderer(String channelId,  List<String> peersId) throws TransactionException, InvalidArgumentException, HFClientContextException {
        return createChannel(channelId, null, peersId);
    }

    public Channel createChannel(String channelId,  String ordererId, String peerId) throws InvalidArgumentException, TransactionException, HFClientContextException {
        ArrayList<String> peersId = new ArrayList<>();
        if (peerId != null && !peerId.isEmpty()) {
            peersId.add(peerId);
        }
        ArrayList<String> orderersId = new ArrayList<>();
        if (ordererId != null && !ordererId.isEmpty()) {
            orderersId.add(ordererId);
        }
        return createChannel(channelId, orderersId, peersId);
    }

    public Channel createChannel(String channelId,  List<String> orderersId, List<String> peersId) throws InvalidArgumentException, TransactionException, HFClientContextException {
        return createChannel(clientHelper.getHFClient(), channelId, orderersId, peersId, null);
    }

    public Channel createChannel(HFClient client, String channelId, List<String> orderersId, List<String> peersId,
                                 Channel.PeerOptions options) throws InvalidArgumentException, TransactionException, HFClientContextException {

        List<Orderer> orderers = clientHelper.createOrderers(orderersId);
        List<Peer> peers = clientHelper.createPeers(peersId);
        return createChannel(client, channelId, peers, orderers, options);
    }



    public Channel createChannel(String channelId) throws InvalidArgumentException, TransactionException, HFClientContextException {
        return createChannel(clientHelper.getHFClient(), channelId, null);
    }

    public Channel createChannelNonPeer(String channelId) throws TransactionException, InvalidArgumentException, HFClientContextException {
        List<Orderer> orderers = clientHelper.createOrderers();
        return createChannel(clientHelper.getHFClient(), channelId, null, orderers, null);
    }

    public Channel createChannelNonOrderer(String channelId) throws TransactionException, InvalidArgumentException, HFClientContextException {
        Set<String> peersId = chainDataService.getPeersId(channelId);
        List<Peer> peers = clientHelper.createPeers(peersId);
        return createChannel(clientHelper.getHFClient(), channelId, peers, null, null);
    }

    public Channel createChannel(HFClient client, String channelId, Channel.PeerOptions options) throws InvalidArgumentException, TransactionException, HFClientContextException {
        List<Orderer> orderers = clientHelper.createOrderers();
        Set<String> peersId = chainDataService.getPeersId(channelId);
        List<Peer> peers = clientHelper.createPeers(peersId);
        return createChannel(client, channelId, peers, orderers, options);
    }

    private Channel createChannel(HFClient client, String channelId, Collection<Peer> peers, Collection<Orderer> orderers,
                                  Channel.PeerOptions options) throws InvalidArgumentException, TransactionException {
        Channel channel = getChannel(client, channelId);

        //set orderers
        if (orderers != null && !orderers.isEmpty()) {
            channelAddOrderer(channel, orderers);
        }

        //set peers
        if (peers != null && !peers.isEmpty()) {
            channelAddPeer(channel, peers, options);
        }

        channel.initialize();
        return channel;
    }

    private Channel getChannel(HFClient client, String channelId) throws InvalidArgumentException {
        if (StringUtils.isEmpty(channelId)) {
            throw new InvalidArgumentException("Channel name is empty");
        }
        if (client == null) {
            throw new InvalidArgumentException("Client name is null");
        }

        //If there is no channel named {channelId}, create it.
        Channel channel = client.getChannel(channelId);
        if (channel == null || channel.isShutdown()) {
            channel = client.newChannel(channelId);
        }
        return channel;
    }

    private void channelAddPeer(Channel channel, Collection<Peer> peers, Channel.PeerOptions options) {
        Collection<Peer> channelPeers = channel.getPeers();
        for (Peer peer : peers) {
            boolean peerExists = false;
            for (Peer channelPeer : channelPeers) {
                if (channelPeer.getUrl().equals(peer.getUrl())) {
                    peerExists = true;
                    break;
                }
            }
            if (!peerExists) {
                try {
                    if (options == null) {
                        channel.addPeer(peer);
                    } else {
                        channel.addPeer(peer, options);
                    }
                } catch (InvalidArgumentException e) {
                    String msg = String.format("Channel %s add peer %s failed.", channel.getName(), peer.getName());
                    LOGGER.error(msg, e);
                }
            }
        }
    }

    private void channelAddOrderer(Channel channel, Collection<Orderer> orderers) {
        Collection<Orderer> channelOrderers = channel.getOrderers();
        for (Orderer orderer : orderers) {
            boolean ordererExists = false;
            for (Orderer channelOrderer : channelOrderers) {
                if (channelOrderer.getUrl().equals(orderer.getUrl())) {
                    ordererExists = true;
                    break;
                }
            }
            if (!ordererExists) {
                try {
                    channel.addOrderer(orderer);
                } catch (InvalidArgumentException e) {
                    String msg = String.format("Channel %s add orderer %s failed.",  channel.getName(), orderer.getName());
                    LOGGER.warn(msg, e);
                }
            }
        }
    }

}
