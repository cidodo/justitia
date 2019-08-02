package org.hyperledger.justitia.identity.service.msp;

import org.hyperledger.justitia.common.bean.node.Node;
import org.hyperledger.justitia.identity.exception.MspException;
import org.hyperledger.justitia.common.face.service.identity.msp.MspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Service
public class MspServiceImpl implements MspService{
    private final MspHelper mspHelper;

    @Autowired
    public MspServiceImpl(MspHelper mspHelper) {
        this.mspHelper = mspHelper;
    }

    @Override
    public File getOrganizationMSP(File saveDir) throws IOException, MspException {
        return new File(mspHelper.generateOrgMsp(saveDir.getPath()));
    }

    @Override
    public File getNodeMSP(File saveDir, String nodeId, Node.NodeType nodeType) throws IOException,
            CertificateException, NoSuchAlgorithmException, MspException {
        return new File(mspHelper.generateNodeMsp(saveDir.getPath(), nodeId, nodeType));
    }
}
