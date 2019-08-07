package org.hyperledger.justitia.identity.service;

import org.hyperledger.justitia.common.bean.node.CouchdbInfo;
import org.hyperledger.justitia.common.bean.node.OrdererInfo;
import org.hyperledger.justitia.common.bean.node.PeerInfo;
import org.hyperledger.justitia.common.face.service.identity.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class NodeServiceImpl implements NodeService {
    private final IdentityConfig identityConfig;

    @Autowired
    public NodeServiceImpl(IdentityConfig identityConfig) {
        this.identityConfig = identityConfig;
    }

    @Override
    public Collection<PeerInfo> getPeersInfo() {
        return identityConfig.getPeers();
    }

    @Override
    public PeerInfo getPeerInfo(String peerId) {
        return identityConfig.getPeer(peerId);
    }

    @Override
    public void setPeer(PeerInfo peerInfo) {
        identityConfig.setPeer(peerInfo);
    }

    @Override
    public void updatePeerInfo(PeerInfo peerInfo) {
        identityConfig.updatePeer(peerInfo);
    }

    @Override
    public void deletePeer(String peerId) {
        identityConfig.deletePeer(peerId);
    }

    @Override
    public Collection<OrdererInfo> getOrderersInfo() {
        return identityConfig.getOrderers();
    }

    @Override
    public OrdererInfo getOrdererInfo() {
        return identityConfig.getOrderer();
    }

    @Override
    public OrdererInfo getOrdererInfo(String ordererId) {
        return identityConfig.getOrderer(ordererId);
    }

    @Override
    public void setOrderer(OrdererInfo ordererInfo) {
        identityConfig.setOrderer(ordererInfo);
    }

    @Override
    public void updateOrdererInfo(OrdererInfo ordererInfo) {
        identityConfig.updateOrderer(ordererInfo);
    }

    @Override
    public void deleteOrderer(String ordererId) {
        identityConfig.deleteOrderer(ordererId);
    }

    @Override
    public Collection<CouchdbInfo> getCouchdbsInfo() {
        return null;
    }

    @Override
    public CouchdbInfo getCouchdbInfo(String couchdbId) {
        return null;
    }
}
