package org.hyperledger.justitia.common.face.service.identity;

import org.hyperledger.justitia.common.bean.node.CouchdbInfo;
import org.hyperledger.justitia.common.bean.node.OrdererInfo;
import org.hyperledger.justitia.common.bean.node.PeerInfo;

import java.util.Collection;

public interface NodeService {
    /**
     * peer
     */
    Collection<PeerInfo> getPeersInfo();
    PeerInfo getPeerInfo(String peerId);
    void setPeer(PeerInfo peerInfo);
    void updatePeerInfo(PeerInfo peerInfo);
    void deletePeer(String peerId);

    /**
     * orderer
     */
    Collection<OrdererInfo> getOrderersInfo();
    OrdererInfo getOrdererInfo();
    OrdererInfo getOrdererInfo(String ordererId);
    void setOrderer(OrdererInfo ordererInfo);
    void updateOrdererInfo(OrdererInfo ordererInfo);
    void deleteOrderer(String peerId);

    /**
     * couchdb
     */
    //FIXME couchdb目前没有必要
    Collection<CouchdbInfo> getCouchdbsInfo();
    CouchdbInfo getCouchdbInfo(String couchdbId);
}
