package org.hyperledger.justitia.common.face.dao.mapper;


import org.hyperledger.justitia.common.bean.node.CouchdbInfo;

public interface CouchdbMapper {
    int deleteByPrimaryKey(String id);

    int insert(CouchdbInfo record);

    int insertSelective(CouchdbInfo record);

    CouchdbInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CouchdbInfo record);

    int updateByPrimaryKey(CouchdbInfo record);
}