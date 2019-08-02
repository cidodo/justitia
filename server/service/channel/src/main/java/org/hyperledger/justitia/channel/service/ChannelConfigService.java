package org.hyperledger.justitia.channel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.ByteString;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.protos.msp.MspConfig;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.hyperledger.justitia.common.face.service.fabric.FabricToolsService;

import java.util.*;

public class ChannelConfigService {
    private final ChannelService channelService;
    private final FabricToolsService fabricToolsService;

    public ChannelConfigService(ChannelService channelService, FabricToolsService fabricToolsService) {
        this.channelService = channelService;
        this.fabricToolsService = fabricToolsService;
    }

    JsonNode getChainConfigAsJson(String channelId) {
        byte[] chainConfigBytes = getChainConfigBytes(channelId);
        return decodeChainConfig(chainConfigBytes);
    }

    byte[] getChainConfigBytes(String channelId) {
        try {
            return channelService.getChannelConfigurationBytesFromOrderer(channelId);
        } catch (Throwable e) {
            LOGGER.error("系统通道配置获取失败", e);
            throw new MemberManageException("系统通道配置获取失败");
        }
    }

    JsonNode decodeChainConfig(byte[] configBytes) {
        try {
            String decode = fabricToolsService.decode(configBytes, "CONFIG");
            return new ObjectMapper().readTree(decode);
        } catch (Throwable e) {
            LOGGER.error("通道配置解析失败", e);
            throw new MemberManageException("通道配置解析失败");
        }
    }

    byte[] encodeChainConfig(JsonNode configJson) {
        try {
            return fabricToolsService.encode(configJson.toString(), "Config");
        } catch (Throwable e) {
            LOGGER.error("更新后的通道配置编码失败", e);
            throw new MemberManageException("更新后的通道配置编码失败");
        }
    }

    void addChainConfig(JsonNode baseConfig, String index, String key, JsonNode increment) {
        JsonNode subJsonNode = baseConfig.path(index);
        if (subJsonNode.has(key)) {
            throw new
        }
        ObjectNode objectNode = (ObjectNode) subJsonNode;
        objectNode.set(key, increment);
    }

    void updateChainConfig(JsonNode baseConfig, String path, JsonNode update) {

    }

    void deleteChainConfig(JsonNode baseConfig, String path) {

    }

    byte[] computeUpdateChainConfig(byte[] original, JsonNode updatedJson, String channelId) {
        byte[] updated = encodeChainConfig(updatedJson);
        return computeUpdateChainConfig(original, updated, channelId);
    }

    byte[] computeUpdateChainConfig(byte[] original, byte[] updated, String channelId) {
        try {
            return fabricToolsService.computeUpdate(original, updated, channelId);
        } catch (Throwable e) {
            LOGGER.error("通道配置更新增量计算失败", e);
            throw new MemberManageException("通道配置更新增量计算失败");
        }
    }

    byte[] signUpdateConfigTrans(byte[] updateConfigTrans) {
        try {
            return channelService.getUpdateChannelConfigurationSignature(updateConfigTrans);
        } catch (Throwable e) {
            LOGGER.error("通道配置交易签名失败", e);
            throw new MemberManageException("通道配置交易签名失败");
        }
    }

    void submitConfigUpdate(String channelId, byte[] updateConfigTrans, List<byte[]> signatures) {
        if (null == signatures) {
            signatures = new ArrayList<>();
        }
        signatures.add(signUpdateConfigTrans(updateConfigTrans));

        //submit to orderer
        try {
            channelService.submitChannelConfig(channelId, updateConfigTrans, signatures.toArray(new byte[signatures.size()][]));
        } catch (Throwable e) {
            LOGGER.error("通道配置更新交易提交失败", e);
            throw new MemberManageException("通道配置更新交易提交失败");
        }
    }

    Configtx.Config parseChainConfig(byte[] chainConfig) {
        return Configtx.Config.parseFrom(chainConfig);
    }

    long getConfigSequence(Configtx.Config config) {
        return config.getSequence();
    }

    Set<String> getMembersMspId(Configtx.Config config) {
        Set<String> orgsMsp = new HashSet<>();
        try {
            Map<String, Configtx.ConfigGroup> groupsMap = config.getChannelGroup().getGroupsMap().get("Application").getGroupsMap();
            for (Configtx.ConfigGroup group : groupsMap.values()) {
                ByteString value = group.getValuesMap().get("MSP").getValue();
                ByteString fabricMspConfig = MspConfig.MSPConfig.parseFrom(value).getConfig();
                String msp = MspConfig.FabricMSPConfig.parseFrom(fabricMspConfig).getName();
                orgsMsp.add(msp);
            }
        }catch (Throwable e) {
            LOGGER.error("道配置读取失败，配置格式不正确", e);
            throw new MemberManageException("道配置读取失败，配置格式不正确");
        }
        return orgsMsp;
    }

}
