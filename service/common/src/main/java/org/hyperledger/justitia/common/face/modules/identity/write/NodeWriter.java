package org.hyperledger.justitia.common.face.modules.identity.write;

import org.hyperledger.justitia.common.face.modules.identity.beans.OrdererInfo;
import org.hyperledger.justitia.common.face.modules.identity.beans.PeerInfo;

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
