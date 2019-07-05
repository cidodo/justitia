package org.hyperledger.justitia.identity.service.write;

import org.hyperledger.justitia.identity.service.beans.OrdererInfo;
import org.hyperledger.justitia.identity.service.beans.PeerInfo;

public interface NodeWriter {
    //peer
    void setPeer(PeerInfo peerInfo);
    void updatePeerInfo(PeerInfo peerInfo);
    void deletePeer(String id);

    //orderer
    void setOrderer(OrdererInfo ordererInfo);
    void updateOrdererInfo(OrdererInfo ordererInfo);
    void deleteOrderer(String id);

}
