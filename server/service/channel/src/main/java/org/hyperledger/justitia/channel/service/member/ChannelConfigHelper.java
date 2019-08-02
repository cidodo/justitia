package org.hyperledger.justitia.channel.service.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.protos.msp.MspConfig;
import org.hyperledger.justitia.common.face.service.fabric.FabricToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ChannelConfigHelper {

    private final FabricToolsService fabricToolsService;

    @Autowired
    public ChannelConfigHelper(FabricToolsService fabricToolsService) {
        this.fabricToolsService = fabricToolsService;
    }

    public byte[] updateMember(String channelId, byte[] baseConfigBytes, String memberName, OrgConfigModifyBean modifyBean) {
        Map baseConfig = decodeChainConfig(baseConfigBytes);
        String orgConfig = modifyBean.buildMemberNewConfig(baseConfig);
        return updateConfig(channelId,baseConfigBytes, memberName, orgConfig, MemberManageChaincode.RequestType.MODIFY_ORG_CONFIG);
    }

    public byte[] addMember(String channelId, byte[] baseConfigBytes, String memberName, String orgConfig) {
        return updateConfig(channelId,baseConfigBytes, memberName, orgConfig, MemberManageChaincode.RequestType.ADD_MEMBER);
    }

    public byte[] deleteMember(String channelId, byte[] baseConfigBytes, String memberName) {
        return updateConfig(channelId, baseConfigBytes, memberName, null, MemberManageChaincode.RequestType.DELETE_MEMBER);
    }

    private byte[] updateConfig(String channelId, byte[] baseConfigBytes, String memberName, String orgConfig, MemberManageChaincode.RequestType type) {
        Map baseConfig = decodeChainConfig(baseConfigBytes);
        Map groups = getApplicationGroups(baseConfig);
        if (groups == null) {
            throw new MemberManageException("通道配置读取失败，配置格式不正确");
        }
        switch (type) {
            case ADD_MEMBER:
                if (groups.containsKey(memberName)) {
                    throw new MemberManageException("通道" + channelId + "中已存在名为" + memberName + "的组织");
                } else {
                    Map memberConfig = new ObjectMapper().readValue(orgConfig, Map.class);
                    groups.put(memberName, memberConfig);
                }
                break;
            case DELETE_MEMBER:
                if (groups.containsKey(memberName)) {
                    groups.remove(memberName);
                } else {
                    throw new MemberManageException("通道" + channelId + "中不包含名为" + memberName + "的组织");
                }
                break;
            case MODIFY_ORG_CONFIG:
                if (groups.containsKey(memberName)) {
                    Map memberConfig = new ObjectMapper().readValue(orgConfig, Map.class);
                    groups.replace(memberName, memberConfig);
                } else {
                    throw new MemberManageException("通道" + channelId + "中不包含名为" + memberName + "的组织");
                }
                break;
            default:
                throw new MemberManageException("未知的请求类型：" + type);
        }
        return createTransaction(baseConfigBytes, baseConfig, channelId);
    }

    private Set<String> getMembersMsp(byte[] channelConfigBytes) {
        Set<String> orgsMsp = new HashSet<>();
        try {
            Configtx.Config channelConfig = Configtx.Config.parseFrom(channelConfigBytes);
            Map<String, Configtx.ConfigGroup> groupsMap = channelConfig.getChannelGroup().getGroupsMap().get("Application").getGroupsMap();
            for (Configtx.ConfigGroup group : groupsMap.values()) {
                ByteString value = group.getValuesMap().get("MSP").getValue();
                ByteString config = MspConfig.MSPConfig.parseFrom(value).getConfig();
                String msp = MspConfig.FabricMSPConfig.parseFrom(config).getName();
                orgsMsp.add(msp);
            }
        }catch (Throwable e) {
            LOGGER.error("道配置读取失败，配置格式不正确", e);
            throw new MemberManageException("道配置读取失败，配置格式不正确");
        }
        return orgsMsp;
    }

    private Map decodeChainConfig(byte[] systemChainConfigBytes) {
        try {
            String decode = fabricToolsService.decode(systemChainConfigBytes, "CONFIG");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(decode, Map.class);
        } catch (Throwable e) {
            LOGGER.error("通道配置解析失败", e);
            throw new MemberManageException("通道配置解析失败");
        }
    }

    private Map getApplicationGroups(Map channelConfig) {
        if (channelConfig == null) {
            throw new MemberManageException("通道配置为空");
        }

        try {
            return (Map)((Map)((Map)((Map) channelConfig.get("channel_group")).get("groups")).get("Application")).get("groups");
//            if (channelConfig.containsKey("channel_group")) {
//                Map channel_group = (Map) channelConfig.get("channel_group");
//                if (channel_group != null && channel_group.containsKey("groups")) {
//                    Map groups = (Map) channel_group.get("groups");
//                    if (groups != null && groups.containsKey("Application")) {
//                        Map application = (Map) groups.get("Application");
//                        if (application != null && application.containsKey("groups")) {
//                            return (Map) application.get("groups");
//                        }
//                    }
//                }
//            }
        } catch (Throwable e) {
            throw new MemberManageException("系统通道配置读取失败，配置格式不正确");
        }
    }


    private byte[] createTransaction(byte[] original, Map modifiedMap, String channelId) {
        //编码修改后的配置
        byte[] modifiedConfigBytes;
        try {
            String configStr = new ObjectMapper().writeValueAsString(modifiedMap);
            modifiedConfigBytes = fabricToolsService.encode(configStr, "Config");
        } catch (Throwable e) {
            LOGGER.error("更新后的通道配置编码失败", e);
            throw new MemberManageException("更新后的通道配置编码失败");
        }
        //计算配置增量
        try {
            return fabricToolsService.computeUpdate(original, modifiedConfigBytes, channelId);
        } catch (Throwable e) {
            LOGGER.error("通道配置更新增量计算失败", e);
            throw new MemberManageException("通道配置更新增量计算失败");
        }
    }
}
