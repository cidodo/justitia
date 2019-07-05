package org.hyperledger.justitia.identity.service.read.impl;

import org.hyperledger.justitia.identity.dao.FabricUserDao;
import org.hyperledger.justitia.identity.service.beans.FabricUserInfo;
import org.hyperledger.justitia.identity.service.read.UserReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserReaderImpl implements UserReader {
    private final FabricUserDao userDao;

    @Autowired
    public UserReaderImpl(FabricUserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 获取一个随机的admin user
     */
    @Override
    public FabricUserInfo getAdminUser() {
        return userDao.getAdminUser();
    }

    /**
     * 获取全部的admin user
     */
    @Override
    public List<FabricUserInfo> selectAdminsUser() {
        return userDao.selectAdminsUser();
    }

    /**
     * 获取指定的admin user
     * @param userId
     * @return
     */
    @Override
    public FabricUserInfo getAdminUser(String userId) {
        return userDao.getAdminUser(userId);
    }

    @Override
    public FabricUserInfo getMemberUser() {
        return userDao.getMemberUser();
    }

    @Override
    public List<FabricUserInfo> selectMembersUser() {
        return userDao.selectMembersUser();
    }

    @Override
    public FabricUserInfo getMemberUser(String userId) {
        return userDao.getMemberUser(userId);
    }

    @Override
    public List<FabricUserInfo> selectUsersBase() {
        return userDao.selectUsersBase();
    }
}
