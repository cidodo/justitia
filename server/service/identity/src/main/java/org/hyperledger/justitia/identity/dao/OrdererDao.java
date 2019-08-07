package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.common.bean.identity.crypto.Msp;
import org.hyperledger.justitia.common.bean.node.OrdererInfo;
import org.hyperledger.justitia.common.face.dao.mapper.OrdererMapper;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.notEmpty;
import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.notNull;

@Component
public class OrdererDao {
    private final OrdererMapper ordererMapper;
    private final MspDao mspDao;

    @Autowired
    public OrdererDao(OrdererMapper ordererMapper, MspDao mspDao) {
        this.ordererMapper = ordererMapper;
        this.mspDao = mspDao;
    }

    public int insertOrderer(OrdererInfo ordererInfo) {
        notNull(ordererInfo, "Orderer information is null.");

        //mspInfo
        Msp msp = ordererInfo.getMsp();
        if (null != msp) {
            mspDao.insertMsp(msp);
        }

        try {
            return ordererMapper.insertSelective(ordererInfo);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "orderer", ordererInfo.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    public int updateOrderer(OrdererInfo ordererInfo) {
        notNull(ordererInfo, "Orderer information is null.");

        //mspInfo
        Msp msp = ordererInfo.getMsp();
        if (null != msp) {
            mspDao.updateMsp(msp);
        }

        return ordererMapper.updateByPrimaryKeySelective(ordererInfo);
    }

    public int deleteOrderer(String id) {
        notEmpty(id, "Orderer id is empty.");

        OrdererInfo orderer = getOrderer(id);
        if (null == orderer) {
            throw new IdentityException(IdentityException.ORDERER_DOES_NOT_EXITS, id);
        }


        Msp msp = orderer.getMsp();
        if (null != msp) {
            mspDao.deleteMspById(msp.getId());
        }
        return ordererMapper.deleteByPrimaryKey(id);
    }

    public List<OrdererInfo> selectOrderers() {
        return ordererMapper.selectOrderersWithCrypto();
    }


    public OrdererInfo getOrderer(String id) {
        notEmpty(id, "Orderer id is empty.");
        return ordererMapper.getOrdererWithCrypto(id);
    }

}
