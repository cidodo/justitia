package org.hyperledger.justitia.common.face.dao.mapper;


import org.hyperledger.justitia.common.bean.node.PeerInfo;

import java.util.List;

public interface PeerMapper {
    int deleteByPrimaryKey(String id);

    int insert(PeerInfo record);

    int insertSelective(PeerInfo record);

    PeerInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PeerInfo record);

    int updateByPrimaryKey(PeerInfo record);






    List<PeerInfo> selectPeers();
    List<PeerInfo> selectPeersWithTls();
    List<PeerInfo> selectPeersWithCrypto();
    PeerInfo getPeer(String id);
    PeerInfo getPeerWithTls(String id);
    PeerInfo getPeerWithCrypto(String id);
}