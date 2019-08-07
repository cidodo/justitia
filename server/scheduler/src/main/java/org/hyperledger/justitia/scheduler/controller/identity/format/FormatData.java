package org.hyperledger.justitia.scheduler.controller.identity.format;

import org.hyperledger.justitia.common.RequestContext;
import org.hyperledger.justitia.common.bean.identity.*;
import org.hyperledger.justitia.common.bean.identity.crypto.Ca;
import org.hyperledger.justitia.common.bean.identity.crypto.Crypto;
import org.hyperledger.justitia.common.bean.identity.crypto.Msp;
import org.hyperledger.justitia.common.bean.node.*;
import org.hyperledger.justitia.scheduler.utils.MultipartFileUtils;
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

    private static void formatNodeInfo(Node nodeInfo, SetNodeBean nodeBean) {
        if (null == nodeInfo || null == nodeBean) {
            return;
        }
        nodeInfo.setId(nodeBean.getName());
        nodeInfo.setOrganizationId(RequestContext.getOrganizationId());
        nodeInfo.setIp(nodeBean.getIp());
        nodeInfo.setPort(nodeBean.getPort());
        Container container = new Container();
        container.setContainerName(nodeBean.getContainerId());

        nodeInfo.setTlsEnable(nodeBean.getTlsEnable());
        nodeInfo.setDoubleVerity(nodeBean.getDoubleVerify());
        nodeInfo.setHostsName(nodeBean.getHostsName());

        formatCrypto(nodeInfo, nodeBean);
    }

    public static FabricUser formatUser(SetUserBean userBean) {
        if (null == userBean) {
            return null;
        }

        FabricUser fabricUser = new FabricUser();
        fabricUser.setId(userBean.getUserName());
        fabricUser.setAdmin(userBean.getAdmin());
        fabricUser.setTag(null);
        formatCrypto(fabricUser, userBean);
        return fabricUser;
    }

    public static Organization formatOrganization(SetOrganizationBean organizationBean) {
        if (null == organizationBean) {
            return null;
        }

        Organization organization = new Organization();
        organization.setId(organizationBean.getName());
        organization.setName(organizationBean.getName());
        organization.setMspId(organizationBean.getMspId());
        organization.setType(organizationBean.getType());
        organization.setTlsEnable(organizationBean.getTlsEnable());
        formatCrypto(organization, organizationBean);
        return organization;
    }

    private static void formatCrypto(Crypto crypto, CryptoBean cryptoBean) {
        String caCert = getMultipartFileString(cryptoBean.getCaCert());
        String caKey = getMultipartFileString(cryptoBean.getCaKey());
        String tlsCaCert = getMultipartFileString(cryptoBean.getTlsCaCert());
        String tlsCaKey = getMultipartFileString(cryptoBean.getTlsCaKey());
        String tlsCert = getMultipartFileString(cryptoBean.getTlsCert());
        String tlsKey = getMultipartFileString(cryptoBean.getTlsKey());

        String signCert = getMultipartFileString(cryptoBean.getSignCerts());
        String signKey = getMultipartFileString(cryptoBean.getSignKey());

        Msp msp = new Msp();
        msp.setCaCerts(caCert);
        msp.setTlsCaCerts(tlsCaCert);
        msp.setTlsCerts(tlsCert);
        msp.setTlsKey(tlsKey);
        msp.setSignCerts(signCert);
        msp.setKeyStore(signKey);

        Ca ca = new Ca();
        ca.setCert(caCert);
        ca.setKey(caKey);

        Ca tlsCa = new Ca();
        tlsCa.setCert(tlsCaCert);
        tlsCa.setKey(tlsCaKey);

        crypto.setMsp(msp);
        crypto.setCa(ca);
        crypto.setTlsCa(tlsCa);
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
