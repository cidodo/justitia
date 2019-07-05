package org.hyperledger.justitia.identity.service.write.impl;

import org.hyperledger.justitia.identity.dao.FabricUserDao;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.hyperledger.justitia.identity.service.beans.FabricUserInfo;
import org.hyperledger.justitia.identity.service.write.UserWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserWriterImpl implements UserWriter {
    private final FabricUserDao userDao;

    @Autowired
    public UserWriterImpl(FabricUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void setUser(FabricUserInfo userInfo) {
        int rowCount = userDao.setUser(userInfo);
        if (1 != rowCount) {
            throw new IdentityException("Set user configuration failed.");
        }
    }

    @Override
    public void updateUserInfo(FabricUserInfo userInfo) {
        int rowCount = userDao.updateUserInfo(userInfo);
        if (1 != rowCount) {
            throw new IdentityException("Update user configuration failed.");
        }
    }

    @Override
    public void deleteUser(String userId) {
        int rowCount = userDao.deleteUser(userId);
        if (1 != rowCount) {
            throw new IdentityException("Delete user configuration failed.");
        }
    }
}
