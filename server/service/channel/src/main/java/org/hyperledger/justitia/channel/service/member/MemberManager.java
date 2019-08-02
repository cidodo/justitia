package org.hyperledger.justitia.channel.service.member;

import com.google.protobuf.ByteString;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.protos.msp.MspConfig;
import org.hyperledger.justitia.common.face.service.fabric.ChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MemberManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberManagerBDC.class);

    private final ChannelService channelService;
    private final MemberManageChaincode memberManageChaincode;
    private final ChannelConfigHelper channelConfigHelper;


    @Autowired
    public MemberManager(ChannelService channelService, MemberManageChaincode memberManageChaincode, ChannelConfigHelper channelConfigHelper) {
        this.channelService = channelService;
        this.memberManageChaincode = memberManageChaincode;
        this.channelConfigHelper = channelConfigHelper;
    }

    public String updateMemberConfig(String channelId, String memberName, OrgConfigModifyBean modifyBean, String description) {
        byte[] baseConfigBytes = getChannelConfigBytes(channelId);
        byte[] transaction = channelConfigHelper.updateMember(channelId, baseConfigBytes, memberName, modifyBean);
        return createSignRequest(channelId, baseConfigBytes, transaction, description, MemberManageChaincode.RequestType.MODIFY_ORG_CONFIG);
    }

    public String addMember(String channelId, String memberName, String orgConfig, String description) {
        byte[] baseConfigBytes = getChannelConfigBytes(channelId);
        byte[] transaction = channelConfigHelper.addMember(channelId, baseConfigBytes, memberName, orgConfig);
        return createSignRequest(channelId, baseConfigBytes, transaction, description, MemberManageChaincode.RequestType.ADD_MEMBER);
    }

    public String deleteMember(String channelId, String memberName, String description) {
        byte[] baseConfigBytes = getChannelConfigBytes(channelId);
        byte[] transaction = channelConfigHelper.deleteMember(channelId, baseConfigBytes, memberName);
        return createSignRequest(channelId, baseConfigBytes, transaction, description, MemberManageChaincode.RequestType.DELETE_MEMBER);
    }

    private byte[] getChannelConfigBytes(String channelName) {
        try {
            return channelService.getChannelConfigurationBytes(channelName);
        } catch (Throwable e) {
            LOGGER.error("系统通道配置获取失败", e);
            throw new MemberManageException("系统通道配置获取失败");
        }
    }

    private String createSignRequest(String channelId, byte[] baseConfigBytes, byte[] transaction,
                                     String description, MemberManageChaincode.RequestType type) {
        Configtx.Config channelConfig = Configtx.Config.parseFrom(baseConfigBytes);
        long sequence = channelConfig.getSequence();
        Set<String> membersMsp = getMembersMsp(channelConfig);
        return memberManageChaincode.signRequests(channelId, transaction, type, sequence, description, membersMsp);
    }

    private Set<String> getMembersMsp(Configtx.Config channelConfig) {
        Set<String> orgsMsp = new HashSet<>();
        try {
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

    public void channelConfigTaskResponse(String channelId, String requestId, String requester, byte[] updatedConfigBytes, boolean reject, String reason) {
        byte[] signature;
        try {
            signature = channelService.getUpdateChannelConfigurationSignature(updatedConfigBytes);
        } catch (Throwable e) {
            LOGGER.error("更新通道配置交易签名失败", e);
            throw new MemberManageException("更新通道配置交易签名失败");
        }
        try {
            memberManageChaincode.signResponses(channelId, requestId, requester, reject, signature, reason);
        } catch (MemberManageException e) {
            LOGGER.error("任务响应失败.", e);
            throw new MemberManageException("任务响应失败.");
        }
    }

    public void submitChannelConfigToOrderer(String channelId, byte[] updatedConfigBytes, String requestId) {
        ArrayList<byte[]> signatures = new ArrayList<>();
        List<MemberManageChaincode.SignResponse> signResponses;
        try {
            signResponses = memberManageChaincode.getAllSignResponses(channelId, requestId);
        } catch (MemberManageException e) {
            LOGGER.error("获取其他通道成员的签名数据失败", e);
            throw new MemberManageException("获取其他通道成员的签名数据失败");
        }
        for (MemberManageChaincode.SignResponse response :signResponses) {
            signatures.add(Base64.getDecoder().decode(response.getSignature()));
        }

        //签名配置交易
        try {
            byte[] selfSignature = channelService.getUpdateChannelConfigurationSignature(updatedConfigBytes);
            signatures.add(selfSignature);
        } catch (Throwable e) {
            LOGGER.error("通道配置交易签名失败", e);
            throw new MemberManageException("通道配置交易签名失败");
        }
        //提交交易到orderer节点
        try {
            channelService.submitChannelConfig(channelId, updatedConfigBytes, signatures.toArray(new byte[signatures.size()][]));
        } catch (Throwable e) {
            LOGGER.error("通道配置更新交易提交失败", e);
            throw new MemberManageException("通道配置更新交易提交失败");
        }
    }

}
