package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.common.bean.identity.FabricUser;
import org.hyperledger.justitia.common.bean.identity.crypto.Msp;
import org.hyperledger.justitia.common.face.dao.mapper.FabricUserMapper;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.notEmpty;
import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.notNull;

@Component
public class FabricUserDao {
    private final FabricUserMapper fabricUserMapper;
    private final MspDao mspDao;

    @Autowired
    public FabricUserDao(FabricUserMapper fabricUserMapper, MspDao mspDao) {
        this.fabricUserMapper = fabricUserMapper;
        this.mspDao = mspDao;
    }

    public int insertUser(FabricUser fabricUser) {
        notNull(fabricUser, "Fabric user information is null.");

        //mspInfo
        Msp msp = fabricUser.getMsp();
        if (null != msp) {
            mspDao.insertMsp(msp);
        }

        try{
            return fabricUserMapper.insertSelective(fabricUser);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "fabric user", fabricUser.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    public int updateUser(FabricUser fabricUser) {
        notNull(fabricUser, "Fabric user information is null.");

        //mspInfo
        Msp msp = fabricUser.getMsp();
        if (null != msp) {
            mspDao.updateMsp(msp);
        }

       return fabricUserMapper.updateByPrimaryKeySelective(fabricUser);
    }

    public int deleteUser(String userId) {
        notEmpty(userId, "Fabric user id is empty.");
        FabricUser user = getUser(userId);
        if (null == user) {
            throw new IdentityException(IdentityException.USER_DOES_NOT_EXITS, userId);
        }
        Msp msp = user.getMsp();
        if (null != msp) {
            mspDao.deleteMspById(msp.getId());
        }
        return fabricUserMapper.deleteByPrimaryKey(userId);
    }

    public FabricUser getAdminUser() {
        return fabricUserMapper.getAdminUser();
    }

    public FabricUser getUser(String userId) {
        return fabricUserMapper.selectByPrimaryKey(userId);
    }

    public List<FabricUser> selectAdminsUser() {
        return null;
    }

    public List<FabricUser> selectUser() {
//        List<FabricUserImpl> fabricUsers = fabricUserMapper.();
        return null;
    }

}
