package org.hyperledger.justitia.identity.dao.format;

import org.hyperledger.justitia.dao.bean.FabricUser;
import org.hyperledger.justitia.dao.bean.Msp;
import org.hyperledger.justitia.service.face.identity.bean.FabricUserInfo;
import org.hyperledger.justitia.service.face.identity.bean.crypto.MspInfo;
import org.hyperledger.justitia.service.face.identity.bean.crypto.NodeCrypto;
import org.hyperledger.justitia.service.face.identity.bean.crypto.TlsInfo;
import org.hyperledger.justitia.service.face.identity.bean.crypto.UserCrypto;

import java.util.ArrayList;
import java.util.List;

public class MspFormater {
    public static MspInfo msp2MspInfo(Msp mspInfo) {
        return msp2MspInfo(mspInfo, null);
    }

    public static MspInfo msp2MspInfo(Msp msp, List<FabricUser> adminUsers) {
        if (null == msp && null == adminUsers) {
            return null;
        }

        MspInfo mspInfo = new MspInfo();
        if (null != msp) {
            mspInfo.setCaCerts(msp.getCaCerts());
            mspInfo.setConfigFile(msp.getConfigFile());
            mspInfo.setCrls(msp.getCrlsAsList());
            mspInfo.setIntermediateCerts(msp.getIntermediateCertsAsList());
            mspInfo.setKeyStore(msp.getKeyStore());
            mspInfo.setSignCerts(msp.getSignCerts());
            mspInfo.setTlsCaCerts(msp.getTlsCaCerts());
            mspInfo.setTlsIntermediateCerts(msp.getTlsIntermediateCertsAsList());
        }

        List<FabricUserInfo> adminsInfo = null;
        if (null != adminUsers) {
            adminsInfo = new ArrayList<>();
            for (FabricUser userInfo : adminUsers) {
                FabricUserInfo admin = FabricUserFormater.fabricUser2UserInfo(userInfo);
                if (null != admin) {
                    adminsInfo.add(admin);
                }
            }
        }
        mspInfo.setAdminCerts(adminsInfo);
        return mspInfo;
    }

    public static NodeCrypto msp2NodeCrypto(Msp msp) {
        if (null == msp ) {
            return null;
        }

        NodeCrypto crypto = new NodeCrypto();

        crypto.setMspInfo(MspFormater.msp2MspInfo(msp));
        crypto.setTlsInfo(MspFormater.msp2TlsInfo(msp));
        return crypto;
    }

    public static TlsInfo msp2TlsInfo(Msp msp) {
        if (null == msp) {
            return null;
        }

        TlsInfo tlsInfo = new TlsInfo();
        tlsInfo.setCa(msp.getTlsCaCerts());
        tlsInfo.setCert(msp.getTlsCerts());
        tlsInfo.setKey(msp.getTlsKey());
        return tlsInfo;
    }

    public static UserCrypto msp2UserCrypto(Msp msp) {
        if (msp == null) {
            return null;
        }
        UserCrypto crypto = new UserCrypto();

        MspInfo mspBean = MspFormater.msp2MspInfo(msp);
        crypto.setMspInfo(mspBean);

        String tlsCaCerts = msp.getTlsCaCerts();
        String tlsCerts = msp.getTlsCerts();
        String tlsKey = msp.getTlsKey();
        TlsInfo tls = null;
        if (null != tlsCaCerts && null != tlsCerts && null != tlsKey) {
            tls = new TlsInfo();
            tls.setCa(tlsCaCerts);
            tls.setCert(tlsCerts);
            tls.setKey(tlsKey);
        }
        crypto.setTlsInfo(tls);
        return crypto;
    }

    public static Msp mspInfo2Msp(String id, MspInfo mspInfo) {
        return mspInfo2Msp(id, mspInfo, null);
    }

    public static Msp mspInfo2Msp(String id, MspInfo mspInfo, TlsInfo tlsInfo) {
        if (null == id) {
            return null;
        }
        Msp msp = new Msp();
        msp.setId(id);
        if (null != mspInfo) {
            //fixme adminUserId
            msp.setAdminUsersIdList(null);


            if (null != mspInfo.getCaCerts()) {
                msp.setCaCerts(mspInfo.getCaCerts());
            }

            if (null != mspInfo.getIntermediateCerts()) {
                msp.setIntermediateCertsList(mspInfo.getIntermediateCerts());
            }

            if (null != mspInfo.getCrls()) {
                msp.setCrlsList(mspInfo.getCrls());
            }

            if (null != mspInfo.getConfigFile()) {
                msp.setConfigFile(mspInfo.getConfigFile());
            }

            if (null != mspInfo.getKeyStore()) {
                msp.setKeyStore(mspInfo.getKeyStore());
            }

            if (null != mspInfo.getSignCerts()) {
                msp.setSignCerts(mspInfo.getSignCerts());
            }

            if (null != mspInfo.getTlsCaCerts()) {
                msp.setTlsCaCerts(mspInfo.getTlsCaCerts());
            }

            if (null != mspInfo.getIntermediateCerts()) {
                msp.setIntermediateCertsList(mspInfo.getIntermediateCerts());
            }

            if (null != mspInfo.getTlsCaCerts()) {
                msp.setTlsCaCerts(mspInfo.getTlsCaCerts());
            }

            if (null != mspInfo.getIntermediateCerts()) {
                msp.setTlsIntermediateCertsList(mspInfo.getIntermediateCerts());
            }

        }

        if (null != tlsInfo) {
            if (null == mspInfo || null == mspInfo.getTlsCaCerts()) {
                msp.setTlsCaCerts(tlsInfo.getCa());
            }
            if (null != tlsInfo.getCert()) {
                msp.setTlsCerts(tlsInfo.getCert());
            }

            if (null != tlsInfo.getKey()) {
                msp.setTlsKey(tlsInfo.getKey());
            }
        }

        return msp;
    }

}
