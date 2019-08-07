package org.hyperledger.justitia.common.face.service.identity.msp;

import org.hyperledger.justitia.common.bean.node.Node;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public interface MspService {
    File getOrganizationMSP(File saveDir);
    File getNodeMSP(File saveDir, String nodeId, Node.NodeType nodeType);
}
