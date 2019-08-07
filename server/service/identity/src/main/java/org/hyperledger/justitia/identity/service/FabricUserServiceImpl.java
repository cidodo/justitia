package org.hyperledger.justitia.identity.service;

import org.hyperledger.justitia.common.bean.identity.FabricUser;
import org.hyperledger.justitia.common.face.service.identity.FabricUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class FabricUserServiceImpl implements FabricUserService {
    private final IdentityConfig identityConfig;

    @Autowired
    public FabricUserServiceImpl(IdentityConfig identityConfig) {
        this.identityConfig = identityConfig;
    }

    @Override
    public Collection<FabricUser> getUsers() {
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
