package org.hyperledger.justitia.identity.dao.format;

import org.hyperledger.justitia.dao.bean.Ca;
import org.hyperledger.justitia.dao.bean.FabricUser;
import org.hyperledger.justitia.dao.bean.Msp;
import org.hyperledger.justitia.dao.bean.Organization;
import org.hyperledger.justitia.common.face.modules.identity.beans.OrganizationInfo;
import org.hyperledger.justitia.common.face.modules.identity.beans.crypto.*;

import java.util.List;


public class OrganizationFormater {
    public static OrganizationInfo organization2OrganizationInfo(Organization organizationInfo) {
        if (organizationInfo == null) {
            return null;
        }
        OrganizationInfo info = new OrganizationInfo();
        info.setId(organizationInfo.getId());
        info.setName(organizationInfo.getName());
        info.setType(organizationInfo.getType());
        info.setMspId(organizationInfo.getMspId());
        info.setTlsEnable(organizationInfo.getTlsEnable());
        OrganizationCrypto crypto = formatOrganizationCrypto(organizationInfo.getCa(), organizationInfo.getTlsCa(),
                organizationInfo.getMsp(), organizationInfo.getAdminUsers());
        info.setCrypto(crypto);
        return info;
    }

    static OrganizationCrypto formatOrganizationCrypto(Ca signCa, Ca tlsCa, Msp msp, List<FabricUser> adminUsers) {
        if (null == signCa && null == tlsCa && null == msp && null == adminUsers) {
            return null;
        }

        OrganizationCrypto crypto = new OrganizationCrypto();
        if (null != signCa) {
            crypto.setCa(CaFormater.ca2CaInfo(signCa));
        }
        if (null != tlsCa) {
            crypto.setTlsca(CaFormater.ca2CaInfo(tlsCa));
        }
        if (null != msp || null != adminUsers) {
            crypto.setMsp(MspFormater.msp2MspInfo(msp, adminUsers));
        }
        //todo NodeInfo
//        crypto.setNodes();
        //todo FabricUser
//        crypto.setUsers();

        return crypto;
    }




}
