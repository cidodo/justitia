package org.hyperledger.justitia.identity.dao.format;

import org.hyperledger.justitia.dao.bean.Ca;
import org.hyperledger.justitia.service.face.identity.bean.crypto.CaInfo;

public class CaFormater {

    public static CaInfo ca2CaInfo(Ca ca) {
        if (null == ca) {
            return null;
        }

        CaInfo caInfo = new CaInfo();
        caInfo.setCert(ca.getCert());
        caInfo.setKey(ca.getKey());
        return caInfo;
    }

}
