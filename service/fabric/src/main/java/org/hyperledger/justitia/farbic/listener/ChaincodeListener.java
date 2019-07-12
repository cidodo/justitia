package org.hyperledger.justitia.farbic.listener;

import org.springframework.stereotype.Service;

@Service
public class ChaincodeListener {
//    private static final Map<String, List<Channel>> registeredChaincode = new ConcurrentHashMap<>();
//    private static HFClient client = null;
//    private final HFClientHelper clientHelper;
//    private final HFChannelHelper channelHelper;
//    private final PeerRefChannel peerRefChannel;
//
//    @Autowired
//    public ChaincodeListener(HFClientHelper clientHelper, HFChannelHelper channelHelper) {
//        this.clientHelper = clientHelper;
//        this.channelHelper = channelHelper;
//    }
//
//    private HFClient getClient() throws HFClientContextException {
//        if (client == null) {
//            synchronized (this) {
//                if (client == null) {
//                    client = clientHelper.createHFClient();
//                }
//            }
//        }
//        return client;
//    }
//
//    private Channel getChannel(String channelId, long startEvent) throws HFClientContextException, TransactionException, InvalidArgumentException {
//        //Fixme 合约监听的channel对象不可以复用,试试channel的addEventHub方法
//        Channel.PeerOptions options = Channel.PeerOptions.createPeerOptions();
//        options.startEvents(startEvent);
//        return channelHelper.createChannel(getClient(), channelId, options);
//    }
//
//    public boolean registerListener(String channelId, Pattern chaincodeId, Pattern eventName, ChaincodeEventListener chaincodeEventListener) {
//        List<String> peersId = peerRefChannel.getPeersId(channelId);
//        return registerListener(channelId, chaincodeId, peersId);
//    }
//
//
//    public boolean registerListener(String channelId, String chaincodeName, List<String> peersId) {
//
//        if (channelId == null || channelId.isEmpty()) {
//            throw new
//        }
//        //todo 检查是否已经注册
//        synchronized (registeredChaincode) {
//            if (registeredChaincode.containsKey(chaincodeName)) {
//                List<Channel> channels = registeredChaincode.get(chaincodeName);
//                for (Channel channel : channels) {
//                    if (channelId.equals(channel.getName())) {
//                        if (channel.isShutdown()) {
//                            channels.remove(channel);
//                        } else {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//
//            if (!registeredChannels.containsKey(channelId)) {
//                if (peersId == null || peersId.isEmpty()) {
//                    throw new Exception("No Peer read can register to listen");
//                }
//                Set<String> installedChaincodePeers = new HashSet<>();
//                for (String peerName : peersName) {
//                    if (checkChaincodeExists(peerName)) {
//                        installedChaincodePeers.add(peerName);
//                    }
//                }
//                if (installedChaincodePeers.isEmpty()) {
//                    throw new Exception("Chaincode" + memberManageChaincodeName + " is not instantiated on peer nodes");
//                }
//
//                long lastConfigBlockNumber;
//                try {
//                    lastConfigBlockNumber = channelManager.getConfigBlockNumber(channelId);
//                } catch (Exception e) {
//                    throw new Exception("Get channel" + channelId + " last config block failed.");
//                }
//
//                Channel channel;
//                try {
//                    channel = getChannel(channelId, installedChaincodePeers, lastConfigBlockNumber);
//                } catch (Exception e) {
//                    throw new Exception("通道对象创建失败，无法在通道" + channelId + "上创建合约" + memberManageChaincodeName + "的SignRequest事件监听失败。");
//                }
//
//                registerSignRequestsListener(channel);
//                registerSignResponsesListener(channel);
//                registerRequestStateListener(channel);
//                registeredChannels.put(channelId, channel);
//
//                getMemberManageTask(channelId);
//                return true;
//            } else {
//                Channel channel = registeredChannels.get(channelId);
//                if (channel.isShutdown()) {
//                    registeredChannels.remove(channelId);
//                    return registerListener(channelId);
//                }
//                return false;
//            }
//    }
//
//    private void registerSignRequestsListener(Pattern chaincodeId, Pattern eventName, ChaincodeEventListener chaincodeEventListener) {
//        try {
//            channel.registerChaincodeEventListener(Pattern.compile("^" + memberManageChaincodeName + "$"),
//                    Pattern.compile("^" + "SignRequest" + "$"), (handle, blockEvent, chaincodeEvent) -> {
//                        ObjectMapper objectMapper = new ObjectMapper();
//                        MemberManageChaincode.SignRequest signRequest;
//                        try {
//                            signRequest = objectMapper.readValue(chaincodeEvent.getPayload(), MemberManageChaincode.SignRequest.class);
//                        } catch (Exception e) {
//                            LOGGER.warn("事件SignRequest返回数据解析失败", e);
//                            return;
//                        }
//                        taskDao.insertChannelConfigTask(signRequest, channel.getName());
//                    });
//        } catch (InvalidArgumentException e) {
//            LOGGER.error("在通道" + channel.getName() + "上创建合约" + memberManageChaincodeName + "的SignRequest事件监听失败。", e);
//        }
//    }
}
