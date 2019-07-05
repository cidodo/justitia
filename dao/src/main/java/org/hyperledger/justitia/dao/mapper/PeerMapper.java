package org.hyperledger.justitia.dao.mapper;

import org.hyperledger.justitia.dao.bean.Peer;

import java.util.List;

public interface PeerMapper {
    int deleteByPrimaryKey(String id);

    int insert(Peer record);

    int insertSelective(Peer record);

    Peer selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Peer record);

    int updateByPrimaryKey(Peer record);






    List<Peer> selectPeers();
    List<Peer> selectPeersWithTls();
    List<Peer> selectPeersWithCrypto();
    Peer getPeer(String id);
    Peer getPeerWithTls(String id);
    Peer getPeerWithCrypto(String id);
}