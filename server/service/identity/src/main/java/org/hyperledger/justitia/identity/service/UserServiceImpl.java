package org.hyperledger.justitia.identity.service;

import org.hyperledger.justitia.common.bean.identity.FabricUser;
import org.hyperledger.justitia.common.face.service.identity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final IdentityConfig identityConfig;

    @Autowired
    public UserServiceImpl(IdentityConfig identityConfig) {
        this.identityConfig = identityConfig;
    }

    @Override
    public List<FabricUser> getUsers() {
        return identityConfig.getUsers();
    }

    @Override
    public FabricUser getAdminUser() {
        return identityConfig.getAdminUser();
    }

    @Override
    public FabricUser getRandomUser() {
        return identityConfig.getUser();
    }

    @Override
    public FabricUser getUser(String userId) {
        return identityConfig.getUser(userId);
    }

    @Override
    public void setUser(FabricUser userInfo) {
        identityConfig.setUser(userInfo);
    }

    @Override
    public void updateUserInfo(FabricUser userInfo) {
        identityConfig.updateUser(userInfo);
    }

    @Override
    public void deleteUser(String userId) {
        identityConfig.deleteUser(userId);
    }
}
