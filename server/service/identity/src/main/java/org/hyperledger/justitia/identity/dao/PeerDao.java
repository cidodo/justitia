package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.common.bean.identity.crypto.Msp;
import org.hyperledger.justitia.common.bean.node.CouchdbInfo;
import org.hyperledger.justitia.common.bean.node.PeerInfo;
import org.hyperledger.justitia.common.face.dao.mapper.PeerMapper;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.notEmpty;
import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.notNull;

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
        notNull(peerInfo, "Peer information is null.");

        //mspInfo
        Msp msp = peerInfo.getMsp();
        if (null != msp) {
            mspDao.insertMsp(msp);
        }

        //couchdb
        CouchdbInfo couchdbInfo = peerInfo.getCouchdb();
        if (null != couchdbInfo) {
            couchdbDao.insertCouchdb(couchdbInfo);
        }

        try {
            return peerMapper.insertSelective(peerInfo);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "peer", peerInfo.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    @Transactional
    public int updatePeer(PeerInfo peerInfo) {
        notNull(peerInfo, "Peer information is null.");

        //mspInfo
        Msp msp = peerInfo.getMsp();
        if (null != msp) {
            mspDao.updateMsp(msp);
        }

        //couchdb
        CouchdbInfo couchdbInfo = peerInfo.getCouchdb();
        if (null != couchdbInfo) {
            couchdbDao.updateCouchdb(couchdbInfo);
        }

        return peerMapper.updateByPrimaryKeySelective(peerInfo);
    }

    @Transactional
    public int deletePeer(String id) {
        notEmpty(id, "Peer id is empty.");

        PeerInfo peer = getPeer(id);
        if (null == peer) {
            throw new IdentityException(IdentityException.PEER_DOES_NOT_EXITS, id);
        }
        //delete couchdb
        CouchdbInfo couchdb = peer.getCouchdb();
        if (null != couchdb) {
            couchdbDao.deleteCouchdbById(couchdb.getId());
        }

        //delete peerMsp
        Msp msp = peer.getMsp();
        if (null != msp) {
            mspDao.deleteMspById(msp.getId());
        }
        //delete peer
        return peerMapper.deleteByPrimaryKey(id);
    }

    public List<PeerInfo> selectPeers() {
        return peerMapper.selectPeersWithCrypto();
    }

    public PeerInfo getPeer(String id) {
        notEmpty(id, "Peer id is empty.");

        return peerMapper.getPeerWithCrypto(id);
    }
}
