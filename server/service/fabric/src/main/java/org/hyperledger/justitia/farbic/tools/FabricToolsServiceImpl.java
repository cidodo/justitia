package org.hyperledger.justitia.farbic.tools;

import org.hyperledger.justitia.common.Context;
import org.hyperledger.justitia.common.utils.file.file.FileUtils;
import org.hyperledger.justitia.farbic.exception.FabricToolsConfigException;
import org.hyperledger.justitia.farbic.exception.FabricToolsException;
import org.hyperledger.justitia.farbic.tools.helper.ConfigtxgenConfigHelper;
import org.hyperledger.justitia.service.face.fabric.FabricToolsService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

@Service
@DependsOn("context")
public class FabricToolsServiceImpl implements FabricToolsService {
    private final File CONFIGTXGEN;
    private final File CONFIGTXGEN_CONFIG_TEMPLATE;
    private final File CONFIGTXLATOR;
    private final File CRYPTOGEN;
    private final File CRYPTOGEN_CONFIG_TEMPLATE;

    public FabricToolsServiceImpl() {
        CONFIGTXGEN = new File(Context.getProperty(Context.FABRIC_TOOLS_CONFIGTXGEN));
        CONFIGTXGEN_CONFIG_TEMPLATE = new File(Context.getProperty(Context.FABRIC_TOOLS_CONFIGTXGEN_CONFIG_TEMPLATE));
        CONFIGTXLATOR = new File(Context.getProperty(Context.FABRIC_TOOLS_CONFIGTXLATOR));
        CRYPTOGEN = new File(Context.getProperty(Context.FABRIC_TOOLS_CRYPTOGEN));
        CRYPTOGEN_CONFIG_TEMPLATE = new File(Context.getProperty(Context.FABRIC_TOOLS_CRYPTOGEN_CONFIG_TEMPLATE));
    }

    /**
     * congiftxgen
     */
    @Override
    public File generateGenesisBlock(String systemChainId, String consortium, ArrayList<String> ordererAddresses) {
        File config = new File(CONFIGTXGEN.getParent() + "configtx.yaml");
        newConfigtxgenConfigHelper().withConsortiumName(consortium)
                .withOrdererAddressed(new HashSet<>(ordererAddresses))
                .generateGenesisBlockConfig(config);

        ConfigTxGen configTxGen = new ConfigTxGen(CONFIGTXGEN, config);
        File output = createTempOutputFile("genesis.block");
        return configTxGen.generateGenesisBlock(systemChainId, output);
    }

    @Override
    public InputStream generateMemberConfig(String organizationName) {
        File config = new File(CONFIGTXGEN.getParent() + "configtx.yaml");
        newConfigtxgenConfigHelper().generateMemberConfigConfig(config);

        ConfigTxGen configTxGen = new ConfigTxGen(CONFIGTXGEN, config);
        return configTxGen.generateMemberConfig(organizationName);
    }

    @Override
    public byte[] generateCreateChannelTx(String channelId, String consortium) {
        File config = new File(CONFIGTXGEN.getParent() + "configtx.yaml");
        newConfigtxgenConfigHelper().withConsortiumName(consortium).generateCreateChannelConfig(config);

        ConfigTxGen configTxGen = new ConfigTxGen(CONFIGTXGEN, config);
        return configTxGen.generateCreateChannelTx(channelId);
    }

    private ConfigtxgenConfigHelper newConfigtxgenConfigHelper() {
        return new ConfigtxgenConfigHelper(CONFIGTXGEN_CONFIG_TEMPLATE)
                .withMember();
    }

    /**
     * configtxlator
     */
    @Override
    public byte[] encode(String data, String protoType) {
        ConfigTxLator configTxLator = new ConfigTxLator(CONFIGTXLATOR);
        return configTxLator.encode(data, transProto(protoType));
    }

    @Override
    public String decode(byte[] data, String protoType) {
        return null;
    }

    @Override
    public byte[] computeUpdate(byte[] original, byte[] updated, String channelName) {
        return new byte[0];
    }

    private ConfigTxLator.ProtoType transProto(String protoType) {
        switch (protoType) {
            case "Block":
                return ConfigTxLator.ProtoType.BLOCK;
            case "Envelope":
                return ConfigTxLator.ProtoType.ENVELOPE;
            case "Config":
                return ConfigTxLator.ProtoType.CONFIG;
            case "ConfigUpdate":
                return ConfigTxLator.ProtoType.CONFIG_UPDATE;
            case "ConfigEnvelope":
                return ConfigTxLator.ProtoType.CONFIG_ENVELOPE;
            case "ConfigUpdateEnvelope":
                return ConfigTxLator.ProtoType.CONFIG_UPDATE_ENVELOPE;
            default:
                throw new IllegalArgumentException("Invalid Proto type:" + protoType);
        }
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

    private File createTempOutputFile(String fileName) throws IOException {
        return FileUtils.createTempFile(fileName);
    }
}
