package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.dao.bean.FabricUser;
import org.hyperledger.justitia.dao.mapper.FabricUserMapper;
import org.hyperledger.justitia.identity.dao.format.FabricUserFormater;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.service.face.identity.bean.FabricUserInfo;
import org.hyperledger.justitia.service.face.identity.bean.crypto.UserCrypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FabricUserDao {
    private final FabricUserMapper fabricUserMapper;
    private final MspDao mspDao;
    private final OrganizationDao organizationDao;

    @Autowired
    public FabricUserDao(FabricUserMapper fabricUserMapper, MspDao mspDao, OrganizationDao organizationDao) {
        this.fabricUserMapper = fabricUserMapper;
        this.mspDao = mspDao;
        this.organizationDao = organizationDao;
    }

    public int insertUser(FabricUserInfo fabricUserInfo) {
        if (null == fabricUserInfo) {
            return  0;
        }

        //mspInfo
        UserCrypto crypto = fabricUserInfo.getCrypto();
        String mspId = null;
        if (null != crypto) {
            mspId = generateMspId(fabricUserInfo.getId());
            if (1 != mspDao.insertPeerMsp(mspId, crypto.getMspInfo(), crypto.getTlsInfo())) {
                mspId = null;
            }
        }

        //fabric user
        //Fixme 不应该支持多组织，但是那样的话表结构要发生变化
        String organizationId = organizationDao.getOrganizationBaseInfo().getId();
        FabricUser fabricUser = FabricUserFormater.FabricUserInfo2FabricUser(fabricUserInfo, mspId, organizationId);

        try{
            return fabricUserMapper.insertSelective(fabricUser);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "fabric user", fabricUser.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    public int updateUser(FabricUserInfo fabricUserInfo) {
        if (null == fabricUserInfo) {
            return  0;
        }

        //mspInfo
        UserCrypto crypto = fabricUserInfo.getCrypto();
        if (null != crypto) {
            mspDao.updateUserMsp(generateMspId(fabricUserInfo.getId()), crypto.getMspInfo(), crypto.getTlsInfo());
        }

        //fabric user
        FabricUser fabricUser = FabricUserFormater.FabricUserInfo2FabricUser(fabricUserInfo, null, null);
        return fabricUserMapper.updateByPrimaryKey(fabricUser);
    }

    public int deleteUser(String userId) {
        if (null == userId || userId.isEmpty()) {
            return 0;
        }
        //mspInfo
        mspDao.deleteMspById(generateMspId(userId));
        //user
        return fabricUserMapper.deleteByPrimaryKey(userId);
    }

    public FabricUserInfo getAdminUser() {
        FabricUser adminUser = fabricUserMapper.getAdminUser();
        return FabricUserFormater.fabricUser2UserInfo(adminUser);
    }

    public FabricUserInfo getUser(String userId) {
        FabricUser fabricUser = fabricUserMapper.selectByPrimaryKey(userId);
        return FabricUserFormater.fabricUser2UserInfo(fabricUser);
    }

//    public FabricUserInfo getUser() {
//        return null;
//    }

    public List<FabricUserInfo> selectAdminsUser() {
        return null;
    }

    public List<FabricUserInfo> selectUser() {
//        List<FabricUser> fabricUsers = fabricUserMapper.();
//        return FabricUserFormater.fabricUsers2FabricUsersInfo(fabricUsers);
        return null;
    }

    public List<FabricUserInfo> selectUsersBase() {
        List<FabricUser> fabricUsers = fabricUserMapper.selectUsersBase();
        return FabricUserFormater.fabricUsers2FabricUsersInfo(fabricUsers);
    }

    private String generateMspId(String userId) {
        return userId + "-user-msp";
    }
}
