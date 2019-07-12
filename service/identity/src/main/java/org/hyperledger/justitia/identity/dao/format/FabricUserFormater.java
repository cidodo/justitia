package org.hyperledger.justitia.identity.dao.format;

import org.hyperledger.justitia.dao.bean.FabricUser;
import org.hyperledger.justitia.common.face.modules.identity.beans.FabricUserInfo;
import org.hyperledger.justitia.common.face.modules.identity.beans.crypto.UserCrypto;

import java.util.ArrayList;
import java.util.List;

public class FabricUserFormater {
    public static FabricUserInfo fabricUser2UserInfo(FabricUser fabricUser) {
        if (null == fabricUser) {
            return null;
        }
        FabricUserInfo user = new FabricUserInfo();
        user.setId(fabricUser.getId());
        user.setAdmin(fabricUser.getAdmin());
        user.setTag(fabricUser.getTag());
        UserCrypto crypto = MspFormater.msp2UserCrypto(fabricUser.getMsp());
        user.setCrypto(crypto);
        return user;
    }

    public static List<FabricUserInfo> fabricUsers2FabricUsersInfo(List<FabricUser> fabricUsers) {
        if (null == fabricUsers || fabricUsers.isEmpty()) {
            return null;
        }

        List<FabricUserInfo> fabricUsersInfo = new ArrayList<>();
        for (FabricUser fabricUser : fabricUsers) {
            FabricUserInfo fabricUserInfo = fabricUser2UserInfo(fabricUser);
            fabricUsersInfo.add(fabricUserInfo);
        }

        return fabricUsersInfo;
    }

    public static FabricUser FabricUserInfo2FabricUser(FabricUserInfo fabricUserInfo, String mspId, String organizationId) {
        if (null == fabricUserInfo) {
            return null;
        }

        FabricUser fabricUser = new FabricUser();
        if (null != fabricUserInfo.getId()) {
            fabricUser.setId(fabricUserInfo.getId());
        }

        if (null != fabricUserInfo.getAdmin()) {
            fabricUser.setAdmin(fabricUserInfo.getAdmin());
        }

        if (null != organizationId && !organizationId.isEmpty()) {
            fabricUser.setOrganizationId(organizationId);
        }

        if (null != mspId && !mspId.isEmpty()) {
            fabricUser.setMspId(mspId);
        }

        if (null != fabricUserInfo.getTag()) {
            fabricUser.setTag(fabricUserInfo.getTag());
        }

        return fabricUser;
    }
}
