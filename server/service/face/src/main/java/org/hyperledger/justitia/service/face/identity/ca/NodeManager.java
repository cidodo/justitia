package org.hyperledger.justitia.service.face.identity.ca;

public interface NodeManager {
    //peer
    void createPeer();      //创建节点的身份信息

    //orderer
    void createOrderer();   //创建节点的身份信息
}
