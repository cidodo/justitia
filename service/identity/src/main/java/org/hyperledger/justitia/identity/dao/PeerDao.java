package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.dao.bean.Peer;
import org.hyperledger.justitia.dao.mapper.PeerMapper;
import org.hyperledger.justitia.identity.dao.format.PeerFormater;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.service.face.identity.bean.CouchdbInfo;
import org.hyperledger.justitia.service.face.identity.bean.PeerInfo;
import org.hyperledger.justitia.service.face.identity.bean.crypto.NodeCrypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeerDao {
    private final PeerMapper peerMapper;
    private final MspDao mspDao;
    private final CouchdbDao couchdbDao;

    @Autowired
    public PeerDao(PeerMapper peerMapper, MspDao mspDao, CouchdbDao couchdbDao) {
        this.peerMapper = peerMapper;
        this.mspDao = mspDao;
        this.couchdbDao = couchdbDao;
    }

    @Transactional
    public int insertPeer(PeerInfo peerInfo) {
        if (null == peerInfo) {
            return 0;
        }

        //mspInfo
        NodeCrypto crypto = peerInfo.getCrypto();
        String mspId = null;
        if (null != crypto) {
            mspId = generateMspId(peerInfo.getId());
            if (1 != mspDao.insertPeerMsp(mspId, crypto.getMspInfo(), crypto.getTlsInfo())) {
                mspId = null;
            }
        }

        //couchdb
        CouchdbInfo couchdbInfo = peerInfo.getCouchdb();
        String couchdbId = null;
        if (null != couchdbInfo) {
            couchdbId = generateCouchdbId(peerInfo.getId());
            if (1 != couchdbDao.insertCouchdb(couchdbId, couchdbInfo)) {
                couchdbId = null;
            }
        }

        Peer peer = PeerFormater.peerInfo2Peer(peerInfo, mspId, couchdbId);

        try {
            return peerMapper.insertSelective(peer);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "peer", peer.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    @Transactional
    public int updatePeer(PeerInfo peerInfo) {
        if (null == peerInfo) {
            return 0;
        }

        //mspInfo
        NodeCrypto crypto = peerInfo.getCrypto();
        if (null != crypto) {
            mspDao.updatePeerMsp(generateMspId(peerInfo.getId()), crypto.getMspInfo(), crypto.getTlsInfo());
        }

        //couchdb
        CouchdbInfo couchdbInfo = peerInfo.getCouchdb();
        if (null != couchdbInfo) {
            couchdbDao.updateCouchdb(generateCouchdbId(peerInfo.getId()), couchdbInfo);
        }

        Peer peer = PeerFormater.peerInfo2Peer(peerInfo, null, null);
        return peerMapper.updateByPrimaryKey(peer);
    }

    @Transactional
    public int deletePeer(String id) {
        //delete couchdb
        couchdbDao.deleteCouchdbById(generateCouchdbId(id));
        //delete peerMsp
        mspDao.deleteMspById(generateMspId(id));
        //delete peer
        return peerMapper.deleteByPrimaryKey(id);
    }

    public List<PeerInfo> selectPeers() {
        List<Peer> peers = peerMapper.selectPeers();
        return PeerFormater.peers2PeersInfo(peers);
    }

    public List<PeerInfo> selectPeersWithTls() {
        List<Peer> peers = peerMapper.selectPeersWithTls();
        return PeerFormater.peers2PeersInfo(peers);
    }

    public List<PeerInfo> selectPeersWithCrypto() {
        List<Peer> peers = peerMapper.selectPeersWithCrypto();
        return PeerFormater.peers2PeersInfo(peers);
    }

    public PeerInfo getPeer(String id) {
        Peer peer = peerMapper.getPeer(id);
        return PeerFormater.peer2PeerInfo(peer);
    }

    public PeerInfo getPeerWithTls(String id) {
        Peer peer = peerMapper.getPeerWithTls(id);
        return PeerFormater.peer2PeerInfo(peer);
    }

    public PeerInfo getPeerWithCrypto(String id) {
        Peer peer = peerMapper.getPeerWithCrypto(id);
        return PeerFormater.peer2PeerInfo(peer);
    }

    private String generateMspId(String peerId) {
        return peerId + "-peer-msp";
    }

    private String generateCouchdbId(String peerId) {
        return peerId + "-couchdb";
    }

}
