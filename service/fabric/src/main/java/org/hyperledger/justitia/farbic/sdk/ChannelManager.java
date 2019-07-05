package org.hyperledger.justitia.farbic.sdk;

import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.justitia.farbic.exception.HFClientContextException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@Service
public class ChannelManager {
    private final HFClientHelper clientHelper;
    private final HFChannelHelper channelHelper;

    @Autowired
    public ChannelManager(HFClientHelper clientHelper, HFChannelHelper channelHelper) {
        this.clientHelper = clientHelper;
        this.channelHelper = channelHelper;
    }

    public Set<String> queryChannels(String peerId) throws InvalidArgumentException, ProposalException, HFClientContextException {
        HFClient client = clientHelper.getHFClient();
        Peer peer = clientHelper.createPeer(peerId);
        return client.queryChannels(peer);
    }

    public BlockchainInfo getBlockChainInfo(String channelId) throws InvalidArgumentException, ProposalException, HFClientContextException, TransactionException {
        Channel channel = channelHelper.createChannelNonOrderer(channelId);
        return channel.queryBlockchainInfo();
    }

    public byte[] getChannelConfigurationBytes(String channelId) throws TransactionException, HFClientContextException, InvalidArgumentException {
        Channel channel = channelHelper.createChannelNonOrderer(channelId);
        return channel.getChannelConfigurationBytes();
    }

    public long getConfigBlockNumber(String channelId) throws TransactionException, HFClientContextException, InvalidArgumentException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Channel channel = channelHelper.createChannelNonOrderer(channelId);
        Class<?> channelClass = Class.forName("org.hyperledger.fabric.sdk.Channel");
        Method method = channelClass.getDeclaredMethod("getConfigBlock", List.class);
        method.setAccessible(true);
        ArrayList<Peer> peers = new ArrayList<>(channel.getPeers());
        Collections.shuffle(peers);
        Common.Block configBlock = (Common.Block) method.invoke(channel, peers);
        return configBlock.getHeader().getNumber();
    }

    public byte[] getChannelConfigurationBytesFromOrderer(String channelId) throws TransactionException,
            HFClientContextException, InvalidArgumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InvalidProtocolBufferException {

        Channel channel = channelHelper.createChannelNonPeer(channelId);
        Class<?> channelClass = Class.forName("org.hyperledger.fabric.sdk.Channel");
        Method method = channelClass.getDeclaredMethod("getConfigurationBlock");
        method.setAccessible(true);
        Common.Block configBlock = (Common.Block) method.invoke(channel);
        Common.Envelope envelopeRet = Common.Envelope.parseFrom(configBlock.getData().getData(0));
        Common.Payload payload = Common.Payload.parseFrom(envelopeRet.getPayload());
        Configtx.ConfigEnvelope configEnvelope = Configtx.ConfigEnvelope.parseFrom(payload.getData());
        return configEnvelope.getConfig().toByteArray();
    }

    public byte[] getChannelConfigurationSignature(byte[] channelConfigBytes) throws InvalidArgumentException, HFClientContextException {
        ChannelConfiguration channelConfig = new ChannelConfiguration(channelConfigBytes);
        HFClient client = clientHelper.getHFClient();
        User user = client.getUserContext();
        return client.getChannelConfigurationSignature(channelConfig, user);
    }
    public byte[] getUpdateChannelConfigurationSignature(byte[] configBytes) throws InvalidArgumentException, HFClientContextException {
        UpdateChannelConfiguration updateChannelConfig = new UpdateChannelConfiguration(configBytes);
        HFClient client = clientHelper.getHFClient();
        User user = client.getUserContext();
        return client.getUpdateChannelConfigurationSignature(updateChannelConfig, user);
    }

    public void submitChannelConfig(String channelId, byte[] configBytes, byte[]... signers)
            throws TransactionException, HFClientContextException, InvalidArgumentException {
        UpdateChannelConfiguration updateChannelConfig = new UpdateChannelConfiguration(configBytes);
        Channel channel = channelHelper.createChannelNonPeer(channelId);
        channel.updateChannelConfiguration(updateChannelConfig, signers);
    }

    public void createChannel(String channelId, byte[] channelConfigBytes, byte[]... signers) throws HFClientContextException,
            InvalidArgumentException, TransactionException {

        ChannelConfiguration channelConfig = new ChannelConfiguration(channelConfigBytes);
        Orderer orderer = clientHelper.createOrderer();
        if (orderer == null) {
            throw new InvalidArgumentException("Not found any orderer information.");
        }
        HFClient client = clientHelper.getHFClient();
        client.newChannel(channelId, orderer, channelConfig, signers);
    }

    public void peerJoinChannel(String channelId, String peerId) throws HFClientContextException, InvalidArgumentException, ProposalException {
        Orderer orderer = clientHelper.createOrderer();
        if (orderer == null) {
            throw new InvalidArgumentException("Not found any orderer information.");
        }
        Peer peer = clientHelper.createPeer(peerId);
        if (peer == null) {
            throw new InvalidArgumentException("Not found peer by name " + peerId);
        }
        HFClient client = clientHelper.getHFClient();
        Channel channel = client.getChannel(channelId);
        if (null == channel) {
            channel = client.newChannel(channelId);
        }
        channel.addOrderer(orderer);
        channel.joinPeer(peer);
    }

}
