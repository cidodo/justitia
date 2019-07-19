package org.hyperledger.justitia.service.face.identity.bean;

import lombok.Data;
import org.hyperledger.justitia.common.bean.FabricUser;
import org.hyperledger.justitia.service.face.identity.bean.crypto.MspInfo;
import org.hyperledger.justitia.service.face.identity.bean.crypto.UserCrypto;

import java.io.IOException;

@Data
public class FabricUserInfo {
    private String id;
    private Boolean admin;
    private String tag;
    private UserCrypto crypto;
    private String mspId;
    private FabricUser fabricUser;

    public FabricUser getFabricUser () throws IOException {
        if (null ==fabricUser) {
            fabricUser = new FabricUser(id);
            fabricUser.setMspId(mspId);
            MspInfo msp = getCrypto().getMspInfo();
            fabricUser.setEnrollment(msp.getSignCerts(), msp.getKeyStore());
        }
        return fabricUser;
    }
}
