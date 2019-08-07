package org.hyperledger.justitia.channel.service.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.protos.msp.MspConfig;
import org.hyperledger.justitia.channel.exception.ChannelServiceException;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.hyperledger.justitia.common.face.service.fabric.FabricToolsService;

import java.util.*;

import static org.hyperledger.justitia.channel.exception.ChannelServiceException.*;

public class ChannelConfigService {
    private final ChannelService channelService;
    private final FabricToolsService fabricToolsService;

    ChannelConfigService(ChannelService channelService, FabricToolsService fabricToolsService) {
        this.channelService = channelService;
        this.fabricToolsService = fabricToolsService;
    }

    public JsonNode getChainConfigAsJson(String channelId) {
        byte[] chainConfigBytes = getChainConfigBytes(channelId);
        return decodeChainConfig(chainConfigBytes);
    }

    public Configtx.Config getChainConfigAsConfig(String channelId) {
        byte[] chainConfigBytes = getChainConfigBytes(channelId);
        return parseChainConfig(chainConfigBytes);
    }

    public byte[] getChainConfigBytes(String channelId) {
        try {
            return channelService.getChannelConfigurationBytesFromOrderer(channelId);
        } catch (Throwable e) {
            throw new ChannelServiceException(GET_CHAIN_CONFIG_ERROR, e, channelId);
        }
    }

    public JsonNode decodeChainConfig(byte[] configBytes) {
        try {
            String decode = fabricToolsService.decode(configBytes, "CONFIG");
            return new ObjectMapper().readTree(decode);
        } catch (Throwable e) {
            throw new ChannelServiceException(DECODE_CHAIN_CONFIG_ERROR, e);
        }
    }

    public byte[] encodeChainConfig(JsonNode configJson) {
        try {
            return fabricToolsService.encode(configJson.toString(), "Config");
        } catch (Throwable e) {
            throw new ChannelServiceException(ENCODE_CHAIN_CONFIG_ERROR, e);
        }
    }

    public void addChainConfig(String channelId, JsonNode baseConfig, String index, String key, JsonNode increment) {
        JsonNode subJsonNode = baseConfig.path(index);
        if (subJsonNode.isMissingNode()) {
            throw new ChannelServiceException(NOT_EXIST_PATH_IN_CONFIG, index);
        }
        if (subJsonNode.has(key)) {
            throw new ChannelServiceException(CHAIN_MEMBER_EXIST, key, channelId);
        }
        ((ObjectNode) subJsonNode).set(key, increment);
    }

    public void updateChainConfig(String channelId, JsonNode baseConfig, String index, String key, JsonNode update) {
        JsonNode subJsonNode = baseConfig.path(index);
        if (subJsonNode.isMissingNode()) {
            throw new ChannelServiceException(NOT_EXIST_PATH_IN_CONFIG, index);
        }
        if (!subJsonNode.has(key)) {
            throw new ChannelServiceException(CHAIN_MEMBER_NOT_EXIST, key, channelId);
        }
        ((ObjectNode) subJsonNode).set(key, update);
    }

    public void deleteChainConfig(String channelId, JsonNode baseConfig, String index, String key) {
        JsonNode subJsonNode = baseConfig.path(index);
        if (subJsonNode.isMissingNode()) {
            throw new ChannelServiceException(NOT_EXIST_PATH_IN_CONFIG, index);
        }
        if (!subJsonNode.has(key)) {
            throw new ChannelServiceException(CHAIN_MEMBER_NOT_EXIST, key, channelId);
        }
        ((ObjectNode) subJsonNode).remove(key);
    }

    public byte[] computeUpdateChainConfig(byte[] original, JsonNode updatedJson, String channelId) {
        byte[] updated = encodeChainConfig(updatedJson);
        return computeUpdateChainConfig(original, updated, channelId);
    }

    public byte[] computeUpdateChainConfig(byte[] original, byte[] updated, String channelId) {
        try {
            return fabricToolsService.computeUpdate(original, updated, channelId);
        } catch (Throwable e) {
            throw new ChannelServiceException(COMPUTE_UPDATE_ERROR, e);
        }
    }

    public byte[] signUpdateConfigTransaction(byte[] updateConfigTransaction) {
        try {
            return channelService.getUpdateChannelConfigurationSignature(updateConfigTransaction);
        } catch (Throwable e) {
            throw new ChannelServiceException(UPDATE_CHAIN_TRANSACTION_SIGN_ERROR, e);
        }
    }

    public void submitConfigUpdate(String channelId, byte[] updateConfigTransaction, List<byte[]> signatures) {
        if (null == signatures) {
            signatures = new ArrayList<>();
        }
        signatures.add(signUpdateConfigTransaction(updateConfigTransaction));

        //submit to orderer
        try {
            channelService.submitChannelConfig(channelId, updateConfigTransaction, signatures.toArray(new byte[signatures.size()][]));
        } catch (Throwable e) {
            throw new ChannelServiceException(UPDATE_TRANSACTION_SUBMIT_ERROR, e);
        }
    }

    public Configtx.Config parseChainConfig(byte[] chainConfig) {
        try {
            return Configtx.Config.parseFrom(chainConfig);
        } catch (InvalidProtocolBufferException e) {
            throw new ChannelServiceException(PARSE_CHAIN_CONFIG_ERROR, e);
        }
    }

    public long getConfigSequence(Configtx.Config config) {
        return config.getSequence();
    }

    public Set<String> getMembersMspId(Configtx.Config config) {
        Set<String> orgsMsp = new HashSet<>();
        try {
            Map<String, Configtx.ConfigGroup> groupsMap = config.getChannelGroup().getGroupsMap().get("Application").getGroupsMap();
            for (Configtx.ConfigGroup group : groupsMap.values()) {
                ByteString value = group.getValuesMap().get("MSP").getValue();
                ByteString fabricMspConfig = MspConfig.MSPConfig.parseFrom(value).getConfig();
                String msp = MspConfig.FabricMSPConfig.parseFrom(fabricMspConfig).getName();
                orgsMsp.add(msp);
            }
        } catch (Throwable e) {
            throw new ChannelServiceException(CHAIN_CONFIG_DATA_ERROR, e);
        }
        return orgsMsp;
    }

}
