package org.hyperledger.justitia.common.face.dao.mapper;


import org.hyperledger.justitia.common.bean.node.Host;

public interface HostMapper {
    int deleteByPrimaryKey(String id);

    int insert(Host record);

    int insertSelective(Host record);

    Host selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Host record);

    int updateByPrimaryKeyWithBLOBs(Host record);

    int updateByPrimaryKey(Host record);
}