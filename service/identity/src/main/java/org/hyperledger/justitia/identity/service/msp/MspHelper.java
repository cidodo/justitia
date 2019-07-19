package org.hyperledger.justitia.identity.service.msp;

import org.hyperledger.justitia.common.utils.StringUtils;
import org.hyperledger.justitia.common.utils.file.file.FileUtils;
import org.hyperledger.justitia.identity.exception.MspException;
import org.hyperledger.justitia.service.face.identity.bean.NodeInfo;
import org.hyperledger.justitia.service.face.identity.bean.OrganizationInfo;
import org.hyperledger.justitia.service.face.identity.bean.FabricUserInfo;
import org.hyperledger.justitia.service.face.identity.bean.crypto.MspInfo;
import org.hyperledger.justitia.service.face.identity.bean.crypto.NodeCrypto;
import org.hyperledger.justitia.service.face.identity.bean.crypto.OrganizationCrypto;
import org.hyperledger.justitia.service.face.identity.bean.crypto.TlsInfo;
import org.hyperledger.justitia.service.face.identity.read.NodeReader;
import org.hyperledger.justitia.service.face.identity.read.OrganizationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

@Service
public class MspHelper {
    private final OrganizationReader organizationReader;
    private final NodeReader nodeReader;

    @Autowired
    public MspHelper(OrganizationReader organizationReader, NodeReader nodeReader) {
        this.organizationReader = organizationReader;
        this.nodeReader = nodeReader;
    }


    public String generateOrgMsp(String saveDir, String organizationId) throws IOException, MspException {
        String mspDir = saveDir + File.separator + "msp";
        FileUtils.makeDirectory(mspDir);
        OrganizationInfo organizationInfoWithCryptoMsp = organizationReader.getOrganizationInfoWithCryptoMsp(organizationId);
        if (null == organizationInfoWithCryptoMsp) {
            throw new MspException(String.format("There is no organization with id %s.", organizationId));
        }
        OrganizationCrypto crypto = organizationInfoWithCryptoMsp.getCrypto();
        if (null == crypto) {
            throw new MspException(String.format("Crypto information is null when organization id is %s.", organizationId));
        }

        try {
            writeOrgMsp(mspDir, crypto.getMsp(), organizationInfoWithCryptoMsp.getTlsEnable());
        } catch (Exception e) {
            FileUtils.delete(mspDir);
            throw e;
        }
        return mspDir;
    }

    private void writeOrgMsp(String mspDir, MspInfo msp, boolean tlsEnable) throws IOException {
        if (msp == null) {
            throw new MspException("MSP information is null.");
        }

        //admincerts
        writeAdminCerts(mspDir, msp.getAdminCerts());
        //cacerts
        writeRootCaCerts(mspDir, msp.getCaCerts(), "cacerts");
        //intermediatecerts
        writeIntermediateCaCerts(mspDir, msp.getIntermediateCerts(), "intermediatecerts");

        // fixme crls

//        if (tlsEnable) {
            //tlscacerts
            writeRootCaCerts(mspDir, msp.getTlsCaCerts(), "tlscacerts");
            //tlsintermediatecerts
            writeIntermediateCaCerts(mspDir, msp.getTlsIntermediateCerts(), "tlsintermediatecerts");
//        }
    }

    /**
     * 构建节点MSP目录
     * @throws IOException              文件写入失败
     * @throws CertificateException     证书文件解析是失败
     * @throws NoSuchAlgorithmException 证书文件解析失败
     */
    public String generateNodeMsp(String saveDir, String nodeId, NodeInfo.NodeType nodeType) throws IOException,
            CertificateException, NoSuchAlgorithmException, MspException {
        NodeInfo nodeInfo;
        if (NodeInfo.NodeType.PEER == nodeType) {
            nodeInfo = nodeReader.getPeerInfoWithCrypto(nodeId);
        } else if (NodeInfo.NodeType.ORDERER == nodeType){
            nodeInfo = nodeReader.getOrdererInfoWithCrypto(nodeId);
        } else {
            throw new MspException(String.format("Illegal read type %s.", nodeType.getOper()));
        }

        if (null == nodeInfo) {
            throw new MspException(String.format("There is no %s with id %s.", nodeType.getOper(), nodeId));
        }

        NodeCrypto crypto = nodeInfo.getCrypto();
        if (null == crypto) {
            throw new MspException(String.format("Crypto information is null when read id is %s and type is  %s.", nodeId, nodeType.getOper()));
        }

        //msp
        String mspDir = saveDir + File.separator + "msp";
        FileUtils.makeDirectory(mspDir);
        try {
            writeNodeMsp(mspDir, crypto.getMspInfo(), nodeInfo.getTlsEnable());
        }catch (Exception e) {
            FileUtils.delete(mspDir);
            throw e;
        }


        //tls
        String tlsDir = saveDir + File.separator + "tls";
        FileUtils.makeDirectory(tlsDir);
        TlsInfo tls = crypto.getTlsInfo();
        if (null != tls) {
            FileUtils.writeStringToFile(tlsDir, tls.getCa(), "ca.crt");
            FileUtils.writeStringToFile(tlsDir, tls.getCert(), "server.crt");
            FileUtils.writeStringToFile(tlsDir, tls.getKey(), "server.key");
        }

        return saveDir;
    }

    private void writeNodeMsp(String mspDir, MspInfo msp, boolean tlsEnable) throws IOException, CertificateException, NoSuchAlgorithmException {
        if (null == msp) {
            throw new MspException("MSP information is null.");
        }
        //admincerts
        writeAdminCerts(mspDir, msp.getAdminCerts());
        //cacerts
        writeRootCaCerts(mspDir, msp.getCaCerts(), "cacerts");
        //intermediatecerts
        writeIntermediateCaCerts(mspDir, msp.getIntermediateCerts(), "intermediatecerts");

        //keystore
        writeKeyStore(mspDir, msp.getSignCerts(), msp.getKeyStore());
        //signcerts
        writeSignCerts(mspDir, msp.getSignCerts());

        //fixme configFile
        //fixme crls

        if (tlsEnable) {
            //tlscacerts
            writeRootCaCerts(mspDir, msp.getTlsCaCerts(), "tlscacerts");
            //tlsintermediatecerts
            writeIntermediateCaCerts(mspDir, msp.getTlsIntermediateCerts(), "tlsintermediatecerts");
        }
    }

    private void writeAdminCerts(String mspDir, List<FabricUserInfo> adminCerts) throws IOException {
        boolean existsAdmin = false;
        if (null != adminCerts) {
            String admincertsPath = mspDir + File.separator + "admincerts";
            FileUtils.makeDirectory(admincertsPath);
            for (FabricUserInfo user : adminCerts) {
                String signCerts = user.getCrypto().getMspInfo().getSignCerts();
                if (StringUtils.isNotEmpty(signCerts)) {
                    String certFileName = user.getId() + "-cert.pem";
                    FileUtils.writeStringToFile(admincertsPath, signCerts, certFileName);
                    existsAdmin = true;
                }
            }
        }
        if (!existsAdmin) {
            throw new MspException("The organization has at least one admin user.");
        }
    }

    private void writeKeyStore(String mspDir, String signCertPem, String signPriKeyPem) throws CertificateException, NoSuchAlgorithmException, IOException {
        if (null == signCertPem || signCertPem.isEmpty()) {
            throw new MspException("SignCerts is null.");
        }
        if (null == signPriKeyPem || signPriKeyPem.isEmpty()) {
            throw new MspException("KeyStore is null.");
        }
        CertFileHelper certHelper = new CertFileHelper();
        String keyFileName = certHelper.getFabricPrivateKeyName(signCertPem);
        String keystorePath = mspDir + File.separator + "keystore";
        FileUtils.writeStringToFile(keystorePath, signPriKeyPem, keyFileName);
    }

    private void writeSignCerts(String mspDir, String signCertPem) throws IOException {
        if (null == signCertPem || signCertPem.isEmpty()) {
            throw new MspException("SignCerts is null.");
        }
        String siancertsPath = mspDir + File.separator + "signcerts";
        FileUtils.writeStringToFile(siancertsPath, signCertPem, "cert.pem");
    }

    private void writeRootCaCerts(String mspDir, String caCerts, String folder) throws IOException {
        if (StringUtils.isNotEmpty(caCerts)) {
            String cacertsPath = mspDir + File.separator + folder;
            FileUtils.makeDirectory(cacertsPath);
            String certFileName = "ca-cert.pem";
            FileUtils.writeStringToFile(cacertsPath, caCerts, certFileName);
        } else {
            throw new MspException("Root CA certificate missing");
        }
    }

    private void writeIntermediateCaCerts(String mspDir, List<String> intermediateCerts, String folder) throws IOException {
        if (null == intermediateCerts || intermediateCerts.isEmpty()) {
            return;
        }
        String intermediatecertsPath = mspDir + File.separator + folder;
        FileUtils.makeDirectory(intermediatecertsPath);
        for (int i = 0; i < intermediateCerts.size(); i++) {
            String intermediateCert = intermediateCerts.get(i);
            String certFileName = "intermediate" + i + "-cert.pem";
            FileUtils.writeStringToFile(intermediatecertsPath, intermediateCert, certFileName);
        }
    }
}
