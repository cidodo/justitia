package org.hyperledger.justitia.farbic.sdk;

public class ChaincodeManager {
    //    private HFClientHelper clientHelper;
//
//    public Collection<ProposalResponse> installChaincode(InstallChaincode installInfo, Set<String> peersName,
//                                                         TransactionRequest.Type language, File chaincodeSourceLoc)
//            throws InvalidArgumentException, ProposalException, ContextException {
//
//        HFClient client = Context.getHfClient();
//        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
//                .setName(installInfo.getChaincodeName())
//                .setVersion(installInfo.getVersion())
//                .setPath(installInfo.getPath())
//                .build();
//        InstallProposalRequest proposalRequest = client.newInstallProposalRequest();
//        proposalRequest.setChaincodeID(chaincodeID);
//        proposalRequest.setChaincodeLanguage(language);
////        proposalRequest.setProposalWaitTime(Config.getConfig().getProposalWaitTime());  //默认35000，也可以自定义
//
//        /*
//          目录 {chaincodeSourceLoc}/src/{chaincodePath}/ 下面包含chaincode的源文件(.go文件)
//          通常{chaincodeSourceLoc}是GOPATH的值。
//         */
//        proposalRequest.setChaincodeSourceLocation(chaincodeSourceLoc);
//
//        Set<Peer> peers = new HashSet<>();
//        for (String peerName : peersName) {
//            Peer peer = Context.getPeer(peerName);
//            if (peer != null) {
//                peers.add(peer);
//            }
//        }
//        return client.sendInstallProposal(proposalRequest, peers);
//    }
//
//    public Object instantiateChaincode(InstantiateChaincode instantiateInfo, ChaincodeEndorsementPolicy policy)
//            throws ProposalException, InvalidArgumentException, ContextException {
//        HFClient client = Context.getHfClient();
//        InstantiateProposalRequest proposalRequest = client.newInstantiationProposalRequest();
//        proposalRequest.setProposalWaitTime(300000);  //默认35000，也可以自定义
//        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
//                .setName(instantiateInfo.getChaincodeName())
//                .setVersion(instantiateInfo.getVersion())
//                .build();
//        proposalRequest.setChaincodeID(chaincodeID);
////        proposalRequest.setChaincodeLanguage(instantiateInfo.getLanguage());
//        proposalRequest.setFcn(instantiateInfo.getFunction());
//        proposalRequest.setArgs(instantiateInfo.getArgs());
//        proposalRequest.setChaincodeEndorsementPolicy(policy);
//
//        //TODO I do not know the purpose of transient map works for.
//        Map<String, byte[]> transientMap = new HashMap<>();
//        transientMap.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(Charset.forName("UTF-8")));
//        transientMap.put("method", "InstantiateProposalRequest".getBytes(Charset.forName("UTF-8")));
//        proposalRequest.setTransientMap(transientMap);
//
//        Channel channel = Context.getChannel(instantiateInfo.getChannelName());
//        ArrayList<String> peersName = instantiateInfo.getPeersName();
//        Peer instantiatePeer = null; //只能用一个节点背书，不然sdk验证不过
//        if (peersName == null || peersName.isEmpty()) {
//            throw new InvalidArgumentException("Peer name is empty.");
//        } else {
//            Collection<Peer> peers = channel.getPeers();
//            if (peers == null || peers.isEmpty()) {
//                throw new ContextException("通道" + instantiateInfo.getChannelName() + "相关的节点丢失，无法实例化合约");
//            }
//            String peerName = peersName.get(0);
//            for(Peer peer : peers) {
//                if (peerName.equals(peer.getName())) {
//                    instantiatePeer = peer;
//                    break;
//                }
//            }
//            if (instantiatePeer == null) {
//                throw new InvalidArgumentException("通道" + instantiateInfo.getChannelName() + "没有关联名为" + peerName + "的peer节点");
//            }
//        }
//
//        HashSet<Peer> peers = new HashSet<>();
//        peers.add(instantiatePeer);
//        Collection<ProposalResponse> proposalResponses = channel.sendInstantiationProposal(proposalRequest, peers);
//        Collection<ProposalResponse> successful = new LinkedList<>();
//        for (ProposalResponse response : proposalResponses) {
//            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
//                successful.add(response);
//            }
//        }
//
//        if (!successful.isEmpty()) {
//            return channel.sendTransaction(successful);
//        } else {
//            return proposalResponses;
//        }
//    }
//
//    public Object upgradeChaincode(InstantiateChaincode upgradeInfo, ChaincodeEndorsementPolicy policy)
//            throws InvalidArgumentException, ProposalException, ContextException {
//
//        HFClient client = Context.getHfClient();
//        UpgradeProposalRequest proposalRequest = client.newUpgradeProposalRequest();
//        proposalRequest.setProposalWaitTime(300000);  //默认35000，也可以自定义
//        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
//                .setName(upgradeInfo.getChaincodeName())
//                .setVersion(upgradeInfo.getVersion())
//                .build();
//        proposalRequest.setChaincodeID(chaincodeID);
////        proposalRequest.setChaincodeLanguage(instantiateInfo.getLanguage());
//        proposalRequest.setFcn(upgradeInfo.getFunction());
//        proposalRequest.setArgs(upgradeInfo.getArgs());
//        proposalRequest.setChaincodeEndorsementPolicy(policy);
//
//        //TODO I do not know the purpose of transient map works for.
//        Map<String, byte[]> transientMap = new HashMap<>();
//        transientMap.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(Charset.forName("UTF-8")));
//        transientMap.put("method", "InstantiateProposalRequest".getBytes(Charset.forName("UTF-8")));
//        proposalRequest.setTransientMap(transientMap);
//
//        Channel channel = Context.getChannel(upgradeInfo.getChannelName());
//        Collection<Peer> peers = channel.getPeers();
//        Collection<ProposalResponse> proposalResponses = channel.sendUpgradeProposal(proposalRequest, peers);
//
//        Collection<ProposalResponse> successful = new LinkedList<>();
//        for (ProposalResponse response : proposalResponses) {
//            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
//                successful.add(response);
//            }
//        }
//
//        if (!successful.isEmpty()) {
//            return channel.sendTransaction(successful);
//        } else {
//            return proposalResponses;
//        }
//    }
//
}
