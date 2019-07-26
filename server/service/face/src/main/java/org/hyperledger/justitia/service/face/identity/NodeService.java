package org.hyperledger.justitia.service.face.identity;

import org.hyperledger.justitia.service.face.identity.bean.CouchdbInfo;
import org.hyperledger.justitia.service.face.identity.bean.OrdererInfo;
import org.hyperledger.justitia.service.face.identity.bean.PeerInfo;

import java.util.List;

public interface NodeService {
    /**
     * peer
     */
    List<PeerInfo> getPeersInfo();
    PeerInfo getPeerInfo(String peerId);
    void setPeer(PeerInfo peerInfo);
    void updatePeerInfo(PeerInfo peerInfo);
    void deletePeer(String peerId);

    /**
     * orderer
     */
    List<OrdererInfo> getOrderersInfo();
    OrdererInfo getOrdererInfo();
    OrdererInfo getOrdererInfo(String ordererId);
    void setOrderer(OrdererInfo ordererInfo);
    void updateOrdererInfo(OrdererInfo ordererInfo);
    void deleteOrderer(String peerId);

    /**
     * couchdb
     */
    //FIXME couchdb目前没有必要
    List<CouchdbInfo> getCouchdbsInfo();
    CouchdbInfo getCouchdbInfo(String couchdbId);
}
