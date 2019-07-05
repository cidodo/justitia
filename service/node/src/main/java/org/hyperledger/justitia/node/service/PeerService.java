package org.hyperledger.justitia.node.service;

import java.util.List;

public interface PeerService {
    void createPeer();
    void setPeer();
    void deletePeer(String peerId);
    void updatePeerInfo();
    List getPeersInfo();
}
