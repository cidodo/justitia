package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.common.bean.identity.crypto.Msp;
import org.hyperledger.justitia.common.face.dao.mapper.MspMapper;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.notEmpty;
import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.notNull;

@Component
public class MspDao {
    private final MspMapper mspMapper;

    @Autowired
    public MspDao(MspMapper mspMapper) {
        this.mspMapper = mspMapper;
    }

    public int insertMsp(Msp msp) {
        notNull(msp, "Msp is null.");
        try{
            return mspMapper.insertSelective(msp);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "msp", msp.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    public int updateMsp(Msp msp) {
        notNull(msp, "Msp is null.");
        return mspMapper.updateByPrimaryKeySelective(msp);
    }

    public int deleteMspById(String id) {
        notEmpty(id, "Id is empty.");
        return mspMapper.deleteByPrimaryKey(id);
    }

}
