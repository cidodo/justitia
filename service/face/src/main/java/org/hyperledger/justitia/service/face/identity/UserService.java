package org.hyperledger.justitia.service.face.identity;

import org.hyperledger.justitia.service.face.identity.bean.FabricUserInfo;

import java.util.List;

public interface UserService {
    List<FabricUserInfo> getUsers();
    FabricUserInfo getAdminUser();
    FabricUserInfo getRandomUser();
    FabricUserInfo getUser(String userId);
    void setUser(FabricUserInfo userInfo);
    void updateUserInfo(FabricUserInfo userInfo);
    void deleteUser(String userId);
}
