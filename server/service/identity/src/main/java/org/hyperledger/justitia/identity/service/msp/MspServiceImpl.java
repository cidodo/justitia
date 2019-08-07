package org.hyperledger.justitia.identity.service.msp;

import org.hyperledger.justitia.common.bean.node.Node;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.hyperledger.justitia.identity.exception.MspException;
import org.hyperledger.justitia.common.face.service.identity.msp.MspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Service
public class MspServiceImpl implements MspService {
    private final MspHelper mspHelper;

    @Autowired
    public MspServiceImpl(MspHelper mspHelper) {
        this.mspHelper = mspHelper;
    }

    @Override
    public File getOrganizationMSP(File saveDir) {
        try {
            return new File(mspHelper.generateOrgMsp(saveDir.getPath()));
        } catch (Throwable e) {
            throw new IdentityException(IdentityException.GENERATE_ORGANIZATION_MSP_ERROR, e);
        }
    }

    @Override
    public File getNodeMSP(File saveDir, String nodeId, Node.NodeType nodeType) {
        try {
            return new File(mspHelper.generateNodeMsp(saveDir.getPath(), nodeId, nodeType));
        } catch (Throwable e) {
            throw new IdentityException(IdentityException.GENERATE_NODE_MSP_ERROR, e, nodeType.getOper(), nodeId);
        }
    }
}
