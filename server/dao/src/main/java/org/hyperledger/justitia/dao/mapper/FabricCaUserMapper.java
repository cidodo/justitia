package org.hyperledger.justitia.dao.mapper;

import org.hyperledger.justitia.dao.bean.FabricCaUser;

public interface FabricCaUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(FabricCaUser record);

    int insertSelective(FabricCaUser record);
}