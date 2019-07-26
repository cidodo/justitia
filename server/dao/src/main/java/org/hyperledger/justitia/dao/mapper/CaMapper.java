package org.hyperledger.justitia.dao.mapper;

import org.hyperledger.justitia.dao.bean.Ca;

public interface CaMapper {
    int deleteByPrimaryKey(String id);

    int insert(Ca record);

    int insertSelective(Ca record);

    Ca selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Ca record);

    int updateByPrimaryKeyWithBLOBs(Ca record);

    int updateByPrimaryKey(Ca record);
}