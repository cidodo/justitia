package org.hyperledger.justitia.scheduler.controller.identity.format;

import org.hyperledger.justitia.scheduler.utils.MultipartFileUtils;
import org.hyperledger.justitia.common.face.modules.identity.beans.crypto.*;
import org.hyperledger.justitia.scheduler.controller.identity.beans.*;
import org.hyperledger.justitia.scheduler.exception.UploadFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FormatData {
    public static PeerInfo formatPeerInfo(SetPeerBean peerBean) {
        if (null == peerBean) {
            return null;
        }
        PeerInfo peerInfo = new PeerInfo();
        formatNodeInfo(peerInfo, peerBean);
        peerInfo.setCouchdbEnable(peerBean.getCouchdbEnable());
//        peerInfo.setCouchdb(formatCouchdbInfo(peerBean.getCouchdb()));
        return peerInfo;
    }

    public static OrdererInfo formatOrdererInfo(SetOrdererBean ordererBean) {
        if (null == ordererBean) {
            return null;
        }
        OrdererInfo ordererInfo = new OrdererInfo();
        formatNodeInfo(ordererInfo, ordererBean);
        ordererInfo.setSystemChainId(ordererBean.getSystemChainId());
        return ordererInfo;
    }

    public static CouchdbInfo formatCouchdbInfo(SetNodeBean couchdbBean) {
        if (null == couchdbBean) {
            return null;
        }
        CouchdbInfo couchdbInfo = new CouchdbInfo();
        formatNodeInfo(couchdbInfo, couchdbBean);
        return couchdbInfo;
    }

    private static void formatNodeInfo(NodeInfo nodeInfo, SetNodeBean nodeBean) {
        if (null == nodeInfo || null == nodeBean) {
            return;
        }
        nodeInfo.setId(nodeBean.getName());
        nodeInfo.setIp(nodeBean.getIp());
        nodeInfo.setPort(nodeBean.getPort());

        NodeCrypto crypto = new NodeCrypto();
        //tlsInfo
        nodeInfo.setTlsEnable(nodeBean.getTlsEnable());
        if (nodeInfo.getTlsEnable()) {
            nodeInfo.setDoubleVerity(nodeBean.getDoubleVerify());
//            nodeInfo.setSslTarget(nodeBean.getSslTarget());

            TlsInfo tlsInfo = new TlsInfo();
            tlsInfo.setCa(getMultipartFileString(nodeBean.getTlsCa()));
            tlsInfo.setCert(getMultipartFileString(nodeBean.getTlsCert()));
            tlsInfo.setKey(getMultipartFileString(nodeBean.getTlsKey()));
            crypto.setTlsInfo(tlsInfo);
        }
        //mspInfo
        crypto.setMspInfo(null);
        nodeInfo.setCrypto(crypto);
    }

    public static FabricUserInfo formatUser(SetUserBean userBean) {
        if (null == userBean) {
            return null;
        }

        FabricUserInfo userInfo = new FabricUserInfo();
        userInfo.setId(userBean.getUserName());
        userInfo.setAdmin(userBean.getAdmin());
        userInfo.setTag(null);
        userInfo.setCrypto(formatCrypto(userBean));
        return userInfo;
    }

    private static UserCrypto formatCrypto(SetUserBean userBean) {
        if (null == userBean) {
            return null;
        }
        UserCrypto crypto = new UserCrypto();
        crypto.setTlsInfo(formatTls(userBean));
        crypto.setMspInfo(formatMsp(userBean));
        return crypto;
    }

    private static TlsInfo formatTls(SetUserBean userBean) {
        if (null == userBean) {
            return null;
        }
        TlsInfo tlsInfo = null;
        if (userBean.getTlsEnable()) {
            tlsInfo = new TlsInfo();
            tlsInfo.setCa(getMultipartFileString(userBean.getTlsCa()));
            tlsInfo.setCert(getMultipartFileString(userBean.getTlsCert()));
            tlsInfo.setKey(getMultipartFileString(userBean.getTlsKey()));
        }
        return tlsInfo;
    }

    private static MspInfo formatMsp(SetUserBean userBean){
        if (null == userBean) {
            return null;
        }
        MspInfo mspInfo = new MspInfo();
        mspInfo.setAdminCerts(null);
        mspInfo.setCaCerts(null);
        mspInfo.setConfigFile(null);
        mspInfo.setCrls(null);
        mspInfo.setIntermediateCerts(null);
        mspInfo.setKeyStore(getMultipartFileString(userBean.getKeyStore()));
        mspInfo.setSignCerts(getMultipartFileString(userBean.getSignCerts()));
        mspInfo.setTlsCaCerts(null);
        mspInfo.setTlsIntermediateCerts(null);
        return mspInfo;
    }

    public static OrganizationInfo formatOrganization(SetOrganizationBean organizationBean) {
        if (null == organizationBean) {
            return null;
        }

        OrganizationInfo organizationInfo = new OrganizationInfo();
        organizationInfo.setId(organizationBean.getName());
        organizationInfo.setName(organizationBean.getName());
        organizationInfo.setMspId(organizationBean.getMspId());
        organizationInfo.setType(organizationBean.getType());
        organizationInfo.setTlsEnable(organizationBean.getTlsEnable());
        organizationInfo.setCrypto(formatOrganizationCrypto(organizationBean));
        return organizationInfo;
    }

    private static OrganizationCrypto formatOrganizationCrypto(SetOrganizationBean organizationBean) {
        if (null == organizationBean) {
            return null;
        }
        String caCert  = getMultipartFileString(organizationBean.getCaCert());
        String tlsCaCert = getMultipartFileString(organizationBean.getTlsCaCert());

        OrganizationCrypto crypto = new OrganizationCrypto();
        CaInfo ca = new CaInfo();
        ca.setCert(caCert);
        crypto.setCa(ca);

        CaInfo tlsCa = new CaInfo();
        tlsCa.setCert(tlsCaCert);
        crypto.setTlsca(tlsCa);

        MspInfo msp = new MspInfo();
        msp.setCaCerts(caCert);
        msp.setTlsCaCerts(tlsCaCert);
        crypto.setMsp(msp);

        return crypto;
    }

    private static String getMultipartFileString(MultipartFile file) {
        if (null == file || file.isEmpty()) {
            throw new UploadFileException("Upload file is empty.");
        }
        try {
            return MultipartFileUtils.readFileAsString(file);
        } catch (IOException e) {
            String msg = String.format("Failed to read upload file %s.", file.getOriginalFilename());
            throw new UploadFileException(msg, e);
        }
    }
}
