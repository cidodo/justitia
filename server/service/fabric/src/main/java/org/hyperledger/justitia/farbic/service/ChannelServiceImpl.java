package org.hyperledger.justitia.farbic.service;

import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.justitia.farbic.exception.FabricServiceException;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@Service
public class ChannelServiceImpl extends FabricServiceHelper implements ChannelService{
    private final HFClientHelper clientHelper;

    @Autowired
    public ChannelServiceImpl(HFClientHelper clientHelper) {
        this.clientHelper = clientHelper;
    }

    @Override
    public Set<String> queryChannels(String peerId) throws InvalidArgumentException, ProposalException {
        HFClient client = clientHelper.getHFClient();
        Peer peer = clientHelper.createPeer(client, peerId);
        return client.queryChannels(peer);
    }

    @Override
    public BlockchainInfo getBlockChainInfo(String channelId) throws InvalidArgumentException, ProposalException {
        Channel channel = clientHelper.createChannel(channelId);
        try {
            return channel.queryBlockchainInfo();
        } finally {
            shutdownChannel(channel, true);
        }
    }

    @Override
    public byte[] getChannelConfigurationBytes(String channelId) throws TransactionException {
        Channel channel = clientHelper.createChannel(channelId);
        try {
            return channel.getChannelConfigurationBytes();
        } finally {
            shutdownChannel(channel, true);
        }
    }

    @Override
    public long getConfigBlockNumber(String channelId) {
        Channel channel = clientHelper.createChannel(channelId);
        try {
            Class<?> channelClass = Class.forName("org.hyperledger.fabric.sdk.Channel");
            Method method = channelClass.getDeclaredMethod("getConfigBlock", List.class);
            method.setAccessible(true);
            ArrayList<Peer> peers = new ArrayList<>(channel.getPeers());
            Collections.shuffle(peers);
            Common.Block configBlock = (Common.Block) method.invoke(channel, peers);
            return configBlock.getHeader().getNumber();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            throw new FabricServiceException(FabricServiceException.UNKONWN_FABRIC_ERROR, e);
        } finally {
            shutdownChannel(channel, true);
        }
    }

    @Override
    public byte[] getChannelConfigurationBytesFromOrderer(String channelId) throws InvalidProtocolBufferException {
        Channel channel = clientHelper.createChannel(channelId);
        Common.Block configBlock;
        try {
            Class<?> channelClass = Class.forName("org.hyperledger.fabric.sdk.Channel");
            Method method = channelClass.getDeclaredMethod("getConfigurationBlock");
            method.setAccessible(true);
            configBlock = (Common.Block) method.invoke(channel);
        } catch (NoSuchMethodException | IllegalAccessException  | InvocationTargetException | ClassNotFoundException e) {
            throw new FabricServiceException(FabricServiceException.UNKONWN_FABRIC_ERROR, e);
        }  finally {
            shutdownChannel(channel, true);
        }
        Common.Envelope envelopeRet = Common.Envelope.parseFrom(configBlock.getData().getData(0));
        Common.Payload payload = Common.Payload.parseFrom(envelopeRet.getPayload());
        Configtx.ConfigEnvelope configEnvelope = Configtx.ConfigEnvelope.parseFrom(payload.getData());
        return configEnvelope.getConfig().toByteArray();
    }

    @Override
    public byte[] getChannelConfigurationSignature(byte[] channelConfigBytes) throws InvalidArgumentException {
        ChannelConfiguration channelConfig = new ChannelConfiguration(channelConfigBytes);
        HFClient client = clientHelper.getHFClient();
        User adminUser = clientHelper.getAdminUser();
        return client.getChannelConfigurationSignature(channelConfig, adminUser);
    }

    @Override
    public byte[] getUpdateChannelConfigurationSignature(byte[] configBytes) throws InvalidArgumentException {
        UpdateChannelConfiguration updateChannelConfig = new UpdateChannelConfiguration(configBytes);
        HFClient client = clientHelper.getHFClient();
        User adminUser = clientHelper.getAdminUser();
        return client.getUpdateChannelConfigurationSignature(updateChannelConfig, adminUser);
    }

    @Override
    public void submitChannelConfig(String channelId, byte[] configBytes, byte[]... signers) throws TransactionException, InvalidArgumentException {
        UpdateChannelConfiguration updateChannelConfig = new UpdateChannelConfiguration(configBytes);
        Channel channel = clientHelper.createChannel(channelId);
        try {
            channel.updateChannelConfiguration(updateChannelConfig, signers);
        } finally {
            shutdownChannel(channel, true);
        }
    }

    @Override
    public void createChannel(String channelId, byte[] channelConfigBytes, byte[]... signers) throws InvalidArgumentException, TransactionException {
        ChannelConfiguration channelConfig = new ChannelConfiguration(channelConfigBytes);
        HFClient client = clientHelper.getHFClient();
        Orderer orderer = clientHelper.createOrderer(client);
        if (orderer == null) {
            throw new InvalidArgumentException("Not found any orderer information.");
        }
        client.newChannel(channelId, orderer, channelConfig, signers);
    }

    @Override
    public void peerJoinChannel(String channelId, String peerId) throws InvalidArgumentException, ProposalException {
        HFClient client = clientHelper.getHFClient();
        Peer peer = clientHelper.createPeer(client, peerId);
        if (peer == null) {
            throw new InvalidArgumentException("Not found peer by name " + peerId);
        }

        Channel channel = clientHelper.createChannel(channelId);
        try {
            channel.joinPeer(peer);
        } finally {
            shutdownChannel(channel, true);
        }
    }

}
