package org.hyperledger.justitia.common.face.modules.identity.msp;

import org.hyperledger.justitia.common.face.modules.identity.beans.NodeInfo;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public interface MspService {
    File generateOrgMsp(String saveDir, String organizationId) throws IOException;
    File generateNodeMsp(String saveDir, String nodeId, NodeInfo.NodeType nodeType) throws IOException, CertificateException, NoSuchAlgorithmException;
}
