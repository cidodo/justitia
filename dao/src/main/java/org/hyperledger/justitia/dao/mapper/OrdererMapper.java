package org.hyperledger.justitia.dao.mapper;

import org.hyperledger.justitia.dao.bean.Orderer;

import java.util.List;

public interface OrdererMapper {
    int deleteByPrimaryKey(String id);

    int insert(Orderer record);

    int insertSelective(Orderer record);

    Orderer selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Orderer record);

    int updateByPrimaryKey(Orderer record);



    //TODO
    List<Orderer> selectOrderers();
    List<Orderer> selectOrderersWithTls();
    List<Orderer> selectOrderersWithCrypto();
    Orderer getOrderer(String id);
    Orderer getOrdererWithTls(String id);
    Orderer getOrdererWithCrypto(String id);
}