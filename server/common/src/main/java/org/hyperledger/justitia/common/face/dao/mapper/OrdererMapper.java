package org.hyperledger.justitia.common.face.dao.mapper;


import org.hyperledger.justitia.common.bean.node.OrdererInfo;

import java.util.List;

public interface OrdererMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrdererInfo record);

    int insertSelective(OrdererInfo record);

    OrdererInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrdererInfo record);

    int updateByPrimaryKey(OrdererInfo record);



    //TODO
    List<OrdererInfo> selectOrderers();
    List<OrdererInfo> selectOrderersWithTls();
    List<OrdererInfo> selectOrderersWithCrypto();
    OrdererInfo getOrderer(String id);
    OrdererInfo getOrdererWithTls(String id);
    OrdererInfo getOrdererWithCrypto(String id);
}