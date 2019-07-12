package org.hyperledger.justitia.common.face.modules.identity.read;

import org.hyperledger.justitia.common.face.modules.identity.beans.FabricUserInfo;

import java.util.List;

public interface UserReader {
    FabricUserInfo getAdminUser();
    List<FabricUserInfo> selectAdminsUser();
    FabricUserInfo getAdminUser(String userId);
    FabricUserInfo getMemberUser();
    List<FabricUserInfo> selectMembersUser();
    FabricUserInfo getMemberUser(String userId);
    List<FabricUserInfo> selectUsersBase();
}
