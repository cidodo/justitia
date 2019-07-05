package org.hyperledger.justitia.identity.service.write;

import org.hyperledger.justitia.identity.service.beans.FabricUserInfo;

public interface UserWriter {
    void setUser(FabricUserInfo userInfo);
    void updateUserInfo(FabricUserInfo userInfo);
    void deleteUser(String userId);
}
