package org.hyperledger.justitia.identity.service.write;

import org.hyperledger.justitia.identity.dao.OrdererDao;
import org.hyperledger.justitia.identity.dao.PeerDao;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.hyperledger.justitia.common.face.modules.identity.beans.OrdererInfo;
import org.hyperledger.justitia.common.face.modules.identity.beans.PeerInfo;
import org.hyperledger.justitia.common.face.modules.identity.write.NodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeWriterImpl implements NodeWriter {
    private final PeerDao peerDao;
    private final OrdererDao ordererDao;

    @Autowired
    public NodeWriterImpl(PeerDao peerDao, OrdererDao ordererDao) {
        this.peerDao = peerDao;
        this.ordererDao = ordererDao;
    }

    @Override
    public void setPeer(PeerInfo peerInfo) {
        int rowCount = peerDao.setPeer(peerInfo);
        if (1 != rowCount) {
            throw new IdentityException("配置Peer节点失败。");
//            throw new IdentityException("Set peer configuration failed.");
        }
    }

    @Override
    public void updatePeerInfo(PeerInfo peerInfo) {
        int rowCount = peerDao.updatePeerInfo(peerInfo);
        if (1 != rowCount) {
            throw new IdentityException("更新Peer节点失败。");
//            throw new IdentityException("Update peer configuration failed.");
        }
    }

    @Override
    public void deletePeer(String id) {
        int rowCount = peerDao.deletePeer(id);
        if (1 != rowCount) {
            throw new IdentityException("删除Peer节点失败。");
//            throw new IdentityException("Delete peer configuration failed.");
        }
    }

    @Override
    public void setOrderer(OrdererInfo ordererInfo) {
        int rowCount = ordererDao.setOrderer(ordererInfo);
        if (1 != rowCount) {
            throw new IdentityException("配置Orderer节点失败。");
//            throw new IdentityException("Set orderer configuration failed.");
        }
    }

    @Override
    public void updateOrdererInfo(OrdererInfo ordererInfo) {
        int rowCount = ordererDao.updateOrdererInfo(ordererInfo);
        if (1 != rowCount) {
            throw new IdentityException("更新Orderer节点失败。");
//            throw new IdentityException("Update orderer configuration failed.");
        }
    }

    @Override
    public void deleteOrderer(String id) {
        int rowCount = ordererDao.deleteOrderer(id);
        if (1 != rowCount) {
            throw new IdentityException("删除Orderer节点失败。");
//            throw new IdentityException("Delete orderer configuration failed.");
        }
    }
}
