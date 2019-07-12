package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.dao.bean.Orderer;
import org.hyperledger.justitia.dao.mapper.OrdererMapper;
import org.hyperledger.justitia.identity.dao.format.OrdererFormater;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.common.face.modules.identity.beans.OrdererInfo;
import org.hyperledger.justitia.common.face.modules.identity.beans.crypto.NodeCrypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrdererDao {
    private final OrdererMapper ordererMapper;
    private final MspDao mspDao;

    @Autowired
    public OrdererDao(OrdererMapper ordererMapper, MspDao mspDao) {
        this.ordererMapper = ordererMapper;
        this.mspDao = mspDao;
    }

    public int setOrderer(OrdererInfo ordererInfo) {
        if (null == ordererInfo) {
            return 0;
        }

        //mspInfo
        NodeCrypto crypto = ordererInfo.getCrypto();
        String mspId = null;
        if (null != crypto) {
            mspId = generateMspId(ordererInfo.getId());
            if (1 != mspDao.insertOrdererMsp(mspId, crypto.getMspInfo(), crypto.getTlsInfo())) {
                mspId = null;
            }
        }

        Orderer orderer = OrdererFormater.ordererInfo2Orderre(ordererInfo, mspId);

        try{
            return ordererMapper.insertSelective(orderer);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "orderer", orderer.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    public int updateOrdererInfo(OrdererInfo ordererInfo) {
        if (null == ordererInfo) {
            return 0;
        }

        //mspInfo
        NodeCrypto crypto = ordererInfo.getCrypto();
        if (null != crypto) {
            mspDao.updateOrdererMsp(generateMspId(ordererInfo.getId()), crypto.getMspInfo(), crypto.getTlsInfo());
        }

        Orderer orderer = OrdererFormater.ordererInfo2Orderre(ordererInfo, null);
        return ordererMapper.updateByPrimaryKey(orderer);
    }

    public int deleteOrderer(String id) {
        //delete mspInfo
        mspDao.deleteMspById(generateMspId(id));
        //delete orderer
        return ordererMapper.deleteByPrimaryKey(id);
    }

    public List<OrdererInfo> selectOrderersInfo() {
        List<Orderer> orderers = ordererMapper.selectOrderers();
        return OrdererFormater.orderers2OrderersInfo(orderers);
    }

    public List<OrdererInfo> selectOrderersInfoWithTls() {
        List<Orderer> orderers = ordererMapper.selectOrderersWithTls();
        return OrdererFormater.orderers2OrderersInfo(orderers);
    }

    public List<OrdererInfo> selectOrderersInfoWithCrypto() {
        List<Orderer> orderers = ordererMapper.selectOrderersWithCrypto();
        return OrdererFormater.orderers2OrderersInfo(orderers);
    }

    public OrdererInfo getOrdererInfo(String id) {
        Orderer orderer = ordererMapper.getOrderer(id);
        return OrdererFormater.orderer2OrdererInfo(orderer);
    }

    public OrdererInfo getOrdererInfoWithTls(String id) {
        Orderer orderer = ordererMapper.getOrdererWithTls(id);
        return OrdererFormater.orderer2OrdererInfo(orderer);
    }

    public OrdererInfo getOrdererInfoWithCrypto(String id) {
        Orderer orderer = ordererMapper.getOrdererWithCrypto(id);
        return OrdererFormater.orderer2OrdererInfo(orderer);
    }

    private String generateMspId(String ordererId) {
        return ordererId + "-orderer-msp";
    }
}
