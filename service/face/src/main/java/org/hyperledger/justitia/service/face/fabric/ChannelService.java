package org.hyperledger.justitia.service.face.fabric;

import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.util.Set;

public interface ChannelService {
    Set<String> queryChannels(String peerId) throws InvalidArgumentException, ProposalException;
    BlockchainInfo getBlockChainInfo(String channelId) throws InvalidArgumentException, ProposalException;
    byte[] getChannelConfigurationBytes(String channelId) throws TransactionException;
    long getConfigBlockNumber(String channelId);
    byte[] getChannelConfigurationBytesFromOrderer(String channelId) throws InvalidProtocolBufferException;
    byte[] getChannelConfigurationSignature(byte[] channelConfigBytes) throws InvalidArgumentException;
    byte[] getUpdateChannelConfigurationSignature(byte[] configBytes) throws InvalidArgumentException;
    void submitChannelConfig(String channelId, byte[] configBytes, byte[]... signers) throws TransactionException, InvalidArgumentException;
    void createChannel(String channelId, byte[] channelConfigBytes, byte[]... signers) throws InvalidArgumentException, TransactionException;
    void peerJoinChannel(String channelId, String peerId) throws InvalidArgumentException, ProposalException;
}
