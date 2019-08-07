package org.hyperledger.justitia.common.face.service.identity;

import org.hyperledger.justitia.common.bean.identity.FabricUser;

import java.util.Collection;

public interface FabricUserService {
    Collection<FabricUser> getUsers();
    FabricUser getAdminUser();
    FabricUser getRandomUser();
    FabricUser getUser(String userId);
    void setUser(FabricUser userInfo);
    void updateUserInfo(FabricUser userInfo);
    void deleteUser(String userId);
}
