package org.hyperledger.justitia.identity.service.msp;

import org.hyperledger.justitia.common.RequestContext;
import org.hyperledger.justitia.common.bean.identity.FabricUser;
import org.hyperledger.justitia.common.bean.identity.Organization;
import org.hyperledger.justitia.common.bean.identity.crypto.Msp;
import org.hyperledger.justitia.common.bean.node.Node;
import org.hyperledger.justitia.common.utils.StringUtils;
import org.hyperledger.justitia.common.utils.file.FileUtils;
import org.hyperledger.justitia.identity.exception.MspException;
import org.hyperledger.justitia.common.face.service.identity.NodeService;
import org.hyperledger.justitia.common.face.service.identity.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

@Service
public class MspHelper {
    private final OrganizationService organizationService;
    private final NodeService nodeService;

    @Autowired
    public MspHelper(OrganizationService organizationService, NodeService nodeReader) {
        this.organizationService = organizationService;
        this.nodeService = nodeReader;
    }


    public String generateOrgMsp(String saveDir) throws IOException, MspException {
        String mspDir = saveDir + File.separator + "msp";
        FileUtils.makeDirectory(mspDir);
        Organization organization = organizationService.getOrganization();
        if (null == organization) {
            throw new MspException(String.format("There is no organization with id %s.", RequestContext.getOrganizationId()));
        }
        Msp msp = organization.getMsp();
        if (null == msp) {
            throw new IllegalArgumentException("Crypto information of organization is null.");
        }
        try {
            writeOrgMsp(mspDir, msp);
        } catch (Throwable  e) {
            FileUtils.delete(mspDir);
            throw e;
        }
        return mspDir;
    }

    private void writeOrgMsp(String output, Msp msp) throws IOException, MspException {
        //admincerts
        writeAdminInfo(output, msp.getAdminUsers());
        //cacerts
        writeRootCaCerts(output, msp.getCaCerts(), "cacerts");
        //intermediatecerts
        writeIntermediateCaCerts(output, msp.getIntermediateCertsAsList(), "intermediatecerts");
        //tlscacerts
        writeRootCaCerts(output, msp.getTlsCaCerts(), "tlscacerts");
        //tlsintermediatecerts
        writeIntermediateCaCerts(output, msp.getTlsIntermediateCertsAsList(), "tlsintermediatecerts");
        // todo crls
    }

    /**
     * 构建节点MSP目录
     * @throws IOException              文件写入失败
     * @throws CertificateException     证书文件解析是失败
     * @throws NoSuchAlgorithmException 证书文件解析失败
     */
    public String generateNodeMsp(String saveDir, String nodeId, Node.NodeType nodeType) throws IOException,
            CertificateException, NoSuchAlgorithmException, MspException {
        Node node;
        if (Node.NodeType.PEER == nodeType) {
            node = nodeService.getPeerInfo(nodeId);
        } else if (Node.NodeType.ORDERER == nodeType){
            node = nodeService.getOrdererInfo(nodeId);
        } else {
            throw new MspException(String.format("Illegal read type %s.", nodeType.getOper()));
        }
        if (null == node) {
            throw new MspException(String.format("There is no %s with id %s.", nodeType.getOper(), nodeId));
        }
        Msp msp = node.getMsp();
        if (null == msp) {
            throw new MspException(String.format("Crypto information of node is null when id is %s.", node.getId()));
        }

        //msp
        String mspDir = saveDir + File.separator + "msp";
        FileUtils.makeDirectory(mspDir);
        try {
            writeNodeMsp(mspDir, msp);
        }catch (Throwable  e) {
            FileUtils.delete(mspDir);
            throw e;
        }

        //tls
        String tlsDir = saveDir + File.separator + "tls";
        FileUtils.makeDirectory(tlsDir);
        FileUtils.writeStringToFile(tlsDir, msp.getTlsCaCerts(), "ca.crt");
        FileUtils.writeStringToFile(tlsDir, msp.getTlsCerts(), "server.crt");
        FileUtils.writeStringToFile(tlsDir, msp.getTlsKey(), "server.key");

        return saveDir;
    }

    private void writeNodeMsp(String output, Msp msp) throws IOException, CertificateException, NoSuchAlgorithmException, MspException {
        //admincerts
        writeAdminInfo(output, msp.getAdminUsers());
        //cacerts
        writeRootCaCerts(output, msp.getCaCerts(), "cacerts");
        //intermediatecerts
        writeIntermediateCaCerts(output, msp.getIntermediateCertsAsList(), "intermediatecerts");
        //keystore
        writeKeyStore(output, msp.getSignCerts(), msp.getKeyStore());
        //signcerts
        writeSignCerts(output, msp.getSignCerts());
        //tlscacerts
        writeRootCaCerts(output, msp.getTlsCaCerts(), "tlscacerts");
        //tlsintermediatecerts
        writeIntermediateCaCerts(output, msp.getTlsIntermediateCertsAsList(), "tlsintermediatecerts");
        //todo configFile
        //todo crls
    }

    private void writeAdminInfo(String mspDir, List<FabricUser> adminUsers) throws IOException, MspException {
        boolean existsAdmin = false;
        if (null != adminUsers) {
            String admincertsPath = mspDir + File.separator + "admincerts";
            FileUtils.makeDirectory(admincertsPath);
            for (FabricUser user : adminUsers) {
                String signCerts = user.getMsp().getSignCerts();
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

    private void writeKeyStore(String mspDir, String signCertPem, String signPriKeyPem) throws CertificateException, NoSuchAlgorithmException, IOException, MspException {
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

    private void writeSignCerts(String mspDir, String signCertPem) throws IOException, MspException {
        if (null == signCertPem || signCertPem.isEmpty()) {
            throw new MspException("SignCerts is null.");
        }
        String siancertsPath = mspDir + File.separator + "signcerts";
        FileUtils.writeStringToFile(siancertsPath, signCertPem, "cert.pem");
    }

    private void writeRootCaCerts(String mspDir, String caCerts, String folder) throws IOException, MspException {
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
