package org.hyperledger.justitia.identity.service.msp;

import org.hyperledger.justitia.identity.exception.MspException;
import org.hyperledger.justitia.common.face.modules.identity.beans.NodeInfo;
import org.hyperledger.justitia.identity.service.msp.MspHelper;
import org.hyperledger.justitia.common.face.modules.identity.msp.MspService;
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
    public File generateOrgMsp(String saveDir, String organizationId) throws IOException, MspException {
        return new File(mspHelper.generateOrgMsp(saveDir, organizationId));
    }

    @Override
    public File generateNodeMsp(String saveDir, String nodeId, NodeInfo.NodeType nodeType) throws IOException,
            CertificateException, NoSuchAlgorithmException, MspException {
        return new File(mspHelper.generateNodeMsp(saveDir, nodeId, nodeType));
    }
}