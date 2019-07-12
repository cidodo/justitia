package org.hyperledger.justitia.common.face.modules.node;

import java.util.List;

public interface PeerService {
    void createPeer();
    void setPeer();
    void deletePeer(String peerId);
    void updatePeerInfo();
    List getPeersInfo();
}
