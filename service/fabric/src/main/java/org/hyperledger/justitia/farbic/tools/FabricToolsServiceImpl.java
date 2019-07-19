package org.hyperledger.justitia.farbic.tools;

import org.hyperledger.justitia.common.Context;
import org.hyperledger.justitia.service.face.fabric.FabricToolsService;
import org.hyperledger.justitia.service.face.identity.OrganizationService;
import org.hyperledger.justitia.service.face.identity.msp.MspService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

@Service
@DependsOn("context")
public class FabricToolsServiceImpl implements FabricToolsService {
    private final String CONFIGTXGEN;
    private final String OCNFIGTXGEN_CONFIG_TEMPLATE;
    private final String CONFIGTXLATOR;
    private final String CRYPTOGEN;
    private final String CRYPTOGEN_CONFIG_TEMPLATE;

    public FabricToolsServiceImpl() {
        CONFIGTXGEN = Context.getProperty(Context.FABRIC_TOOLS_CONFIGTXGEN);
        OCNFIGTXGEN_CONFIG_TEMPLATE = Context.getProperty(Context.FABRIC_TOOLS_CONFIGTXGEN_CONFIG_TEMPLATE);
        CONFIGTXLATOR = Context.getProperty(Context.FABRIC_TOOLS_CONFIGTXLATOR);
        CRYPTOGEN = Context.getProperty(Context.FABRIC_TOOLS_CRYPTOGEN);
        CRYPTOGEN_CONFIG_TEMPLATE = Context.getProperty(Context.FABRIC_TOOLS_CRYPTOGEN_CONFIG_TEMPLATE);
    }

    /**
     * congiftxgen
     */
    @Override
    public File generateGenesisBlock(String systemChainId, String consortium, ArrayList<String> ordererAddresses) {
        return null;
    }

    @Override
    public InputStream generateMemberConfig() {
        return null;
    }

    @Override
    public byte[] generateCreateChannelTx(String channelId, String consortium) {
        return new byte[0];
    }

    /**
     * configtxlator
     */
    @Override
    public byte[] encode(String data, String protoType) {
        return new byte[0];
    }

    @Override
    public String decode(byte[] data, String protoType) {
        return null;
    }

    @Override
    public byte[] computeUpdate(byte[] original, byte[] updated, String channelName) {
        return new byte[0];
    }

    /**
     * cryptogen
     */
    @Override
    public void generateCrypto() {

    }

    @Override
    public void extendCrypto() {

    }

}
