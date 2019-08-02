package org.hyperledger.justitia.common.face.dao.mapper;

import org.hyperledger.justitia.dao.bean.FabricCa;

public interface FabricCaMapper {
    int deleteByPrimaryKey(String id);

    int insert(FabricCa record);

    int insertSelective(FabricCa record);
}