package org.hyperledger.justitia.common.face.dao.mapper;


import org.hyperledger.justitia.common.bean.node.Container;

public interface ContainerMapper {
    int deleteByPrimaryKey(String id);

    int insert(Container record);

    int insertSelective(Container record);

    Container selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Container record);

    int updateByPrimaryKeyWithBLOBs(Container record);

    int updateByPrimaryKey(Container record);
}