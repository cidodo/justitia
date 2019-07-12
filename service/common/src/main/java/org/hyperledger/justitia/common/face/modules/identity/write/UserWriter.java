package org.hyperledger.justitia.common.face.modules.identity.write;

import org.hyperledger.justitia.common.face.modules.identity.beans.FabricUserInfo;

public interface UserWriter {
    void setUser(FabricUserInfo userInfo);
    void updateUserInfo(FabricUserInfo userInfo);
    void deleteUser(String userId);
}
