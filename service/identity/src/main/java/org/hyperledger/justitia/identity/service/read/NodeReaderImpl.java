package org.hyperledger.justitia.identity.service.read;

import org.hyperledger.justitia.identity.dao.OrdererDao;
import org.hyperledger.justitia.identity.dao.PeerDao;
import org.hyperledger.justitia.common.face.modules.identity.beans.CouchdbInfo;
import org.hyperledger.justitia.common.face.modules.identity.beans.OrdererInfo;
import org.hyperledger.justitia.common.face.modules.identity.beans.PeerInfo;
import org.hyperledger.justitia.common.face.modules.identity.read.NodeReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeReaderImpl implements NodeReader {
    private final PeerDao peerDao;
    private final OrdererDao ordererDao;

    @Autowired
    public NodeReaderImpl(PeerDao peerDao, OrdererDao ordererDao) {
        this.peerDao = peerDao;
        this.ordererDao = ordererDao;
    }

    @Override
    public List<PeerInfo> getPeersInfo() {
        return peerDao.selectPeersInfo();
    }

    @Override
    public List<PeerInfo> getPeersInfoWithTls() {
        return peerDao.selectPeersInfoWithTls();
    }

    @Override
    public List<PeerInfo> getPeersInfoWithCrypto() {
        return peerDao.selectPeersInfoWithCrypto();
    }

    @Override
    public PeerInfo getPeerInfo(String peerId) {
        return peerDao.getPeerInfo(peerId);
    }

    @Override
    public PeerInfo getPeerInfoWithTls(String peerId) {
        return peerDao.getPeerInfoWithTls(peerId);
    }

    @Override
    public PeerInfo getPeerInfoWithCrypto(String peerId) {
        return peerDao.getPeerInfoWithCrypto(peerId);
    }

    @Override
    public List<OrdererInfo> getOrderersInfo() {
        return ordererDao.selectOrderersInfo();
    }

    @Override
    public List<OrdererInfo> getOrderersInfoWithTls() {
        return ordererDao.selectOrderersInfoWithTls();
    }

    @Override
    public List<OrdererInfo> getOrderersInfoWithCrypto() {
        return ordererDao.selectOrderersInfoWithCrypto();
    }

    @Override
    public OrdererInfo getOrdererInfo(String ordererId) {
        return ordererDao.getOrdererInfo(ordererId);
    }

    @Override
    public OrdererInfo getOrdererInfoWithTls(String ordererId) {
        return ordererDao.getOrdererInfoWithTls(ordererId);
    }

    @Override
    public OrdererInfo getOrdererInfoWithCrypto(String ordererId) {
        return ordererDao.getOrdererInfoWithCrypto(ordererId);
    }

    @Override
    public List<CouchdbInfo> getCouchdbsInfo() {
        return null;
    }

    @Override
    public List<CouchdbInfo> getCouchdbsInfoWithTls() {
        return null;
    }

    @Override
    public CouchdbInfo getCouchdbInfo(String couchdbId) {
        return null;
    }

    @Override
    public CouchdbInfo getCouchdbInfoWithTls(String couchdbId) {
        return null;
    }
}
