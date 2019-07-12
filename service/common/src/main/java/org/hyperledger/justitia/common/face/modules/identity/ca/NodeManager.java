package org.hyperledger.justitia.common.face.modules.identity.ca;

public interface NodeManager {
    //peer
    void createPeer();      //创建节点的身份信息

    //orderer
    void createOrderer();   //创建节点的身份信息
}
