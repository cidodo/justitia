package org.hyperledger.justitia.identity.service.read;

import org.hyperledger.justitia.identity.service.beans.CouchdbInfo;
import org.hyperledger.justitia.identity.service.beans.OrdererInfo;
import org.hyperledger.justitia.identity.service.beans.PeerInfo;

import java.util.List;

public interface NodeReader {
    //peer
    List<PeerInfo> getPeersInfo();
    List<PeerInfo> getPeersInfoWithTls();
    List<PeerInfo> getPeersInfoWithCrypto();
    PeerInfo getPeerInfo(String peerId);
    PeerInfo getPeerInfoWithTls(String peerId);
    PeerInfo getPeerInfoWithCrypto(String peerId);

    //orderer
    List<OrdererInfo> getOrderersInfo();
    List<OrdererInfo> getOrderersInfoWithTls();
    List<OrdererInfo> getOrderersInfoWithCrypto();
    OrdererInfo getOrdererInfo(String ordererId);
    OrdererInfo getOrdererInfoWithTls(String ordererId);
    OrdererInfo getOrdererInfoWithCrypto(String ordererId);


    //FIXME couchdb目前没有必要
    //couchdb
    List<CouchdbInfo> getCouchdbsInfo();
    List<CouchdbInfo> getCouchdbsInfoWithTls();
    CouchdbInfo getCouchdbInfo(String couchdbId);
    CouchdbInfo getCouchdbInfoWithTls(String couchdbId);
}
