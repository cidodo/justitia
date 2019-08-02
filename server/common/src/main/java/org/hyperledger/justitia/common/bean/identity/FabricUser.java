package org.hyperledger.justitia.common.bean.identity;

import lombok.Data;
import org.hyperledger.justitia.common.bean.FabricUserImpl;
import org.hyperledger.justitia.common.bean.identity.crypto.Crypto;
import org.hyperledger.justitia.common.bean.identity.crypto.Msp;

import java.io.IOException;

@Data
public class FabricUser extends Crypto{
    private String id;
    private String organizationId;
    private Boolean admin;
    private String tag;

    private String mspId;
    private FabricUserImpl fabricUser;

    public FabricUserImpl getFabricUser() throws IOException {
        if (null ==fabricUser) {
            fabricUser = new FabricUserImpl(id);
            fabricUser.setMspId(mspId);
            fabricUser.setEnrollment(getMsp().getSignCerts(), getMsp().getKeyStore());
        }
        return fabricUser;
    }

    @Override
    protected String generateMspId() {
        return id + "-user-msp";
    }

    @Override
    protected String generateCaId() {
        return id + "-user-ca";
    }

    @Override
    protected String generateTlsCaId() {
        return id + "-user-tls-ca";
    }
}