package org.hyperledger.justitia.common.bean.identity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hyperledger.justitia.common.bean.FabricUserImpl;
import org.hyperledger.justitia.common.bean.identity.crypto.Crypto;

import java.io.IOException;

public class FabricUser extends Crypto{
    @Setter @Getter private String id;
    @Setter @Getter private String organizationId;
    @Setter @Getter private Boolean admin;
    @Setter @Getter private String tag;

    @Setter @Getter private String mspId;
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