package org.hyperledger.justitia.common.face.dao.mapper;


import org.hyperledger.justitia.common.bean.identity.crypto.Msp;

public interface MspMapper {
    int deleteByPrimaryKey(String id);

    int insert(Msp record);

    int insertSelective(Msp record);

    Msp selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Msp record);

    int updateByPrimaryKeyWithBLOBs(Msp record);
}