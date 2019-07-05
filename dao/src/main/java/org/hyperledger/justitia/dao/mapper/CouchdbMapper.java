package org.hyperledger.justitia.dao.mapper;

import org.hyperledger.justitia.dao.bean.Couchdb;

public interface CouchdbMapper {
    int deleteByPrimaryKey(String id);

    int insert(Couchdb record);

    int insertSelective(Couchdb record);

    Couchdb selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Couchdb record);

    int updateByPrimaryKey(Couchdb record);
}