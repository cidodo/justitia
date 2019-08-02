package org.hyperledger.justitia.common.face.dao.mapper;

import org.hyperledger.justitia.common.bean.identity.FabricUser;

import java.util.List;

public interface FabricUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(FabricUser record);

    int insertSelective(FabricUser record);

    FabricUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FabricUser record);

    int updateByPrimaryKeyWithBLOBs(FabricUser record);

    int updateByPrimaryKey(FabricUser record);





    FabricUser getAdminUser();
    List<FabricUser> selectUsersBase();
}