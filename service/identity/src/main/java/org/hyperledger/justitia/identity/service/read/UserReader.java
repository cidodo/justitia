package org.hyperledger.justitia.identity.service.read;

import org.hyperledger.justitia.identity.service.beans.FabricUserInfo;

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
