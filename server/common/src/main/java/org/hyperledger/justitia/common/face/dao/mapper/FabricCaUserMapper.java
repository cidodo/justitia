package org.hyperledger.justitia.common.face.dao.mapper;

import org.hyperledger.justitia.dao.bean.FabricCaUser;

public interface FabricCaUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(FabricCaUser record);

    int insertSelective(FabricCaUser record);
}