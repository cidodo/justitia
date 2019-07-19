package org.hyperledger.justitia.identity.service;

import org.hyperledger.justitia.service.face.identity.UserService;
import org.hyperledger.justitia.service.face.identity.bean.FabricUserInfo;
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
    public List<FabricUserInfo> getUsers() {
        return identityConfig.getUsers();
    }

    @Override
    public FabricUserInfo getAdminUser() {
        return identityConfig.getAdminUser();
    }

    @Override
    public FabricUserInfo getRandomUser() {
        return identityConfig.getUser();
    }

    @Override
    public FabricUserInfo getUser(String userId) {
        return identityConfig.getUser(userId);
    }

    @Override
    public void setUser(FabricUserInfo userInfo) {
        identityConfig.setUser(userInfo);
    }

    @Override
    public void updateUserInfo(FabricUserInfo userInfo) {
        identityConfig.updateUser(userInfo);
    }

    @Override
    public void deleteUser(String userId) {
        identityConfig.deleteUser(userId);
    }
}
