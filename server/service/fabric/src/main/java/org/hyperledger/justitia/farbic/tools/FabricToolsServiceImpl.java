package org.hyperledger.justitia.farbic.tools;

import org.hyperledger.justitia.common.Context;
import org.hyperledger.justitia.common.utils.file.FileUtils;
import org.hyperledger.justitia.farbic.exception.FabricServiceException;
import org.hyperledger.justitia.farbic.exception.FabricToolsConfigException;
import org.hyperledger.justitia.farbic.tools.helper.ConfigtxgenConfigHelper;
import org.hyperledger.justitia.farbic.tools.helper.CryptogenConfigHelper;
import org.hyperledger.justitia.common.face.service.fabric.FabricToolsService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import static org.hyperledger.justitia.farbic.exception.FabricServiceException.*;

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

    private File createTempOutputFile(String fileName) throws IOException {
        return FileUtils.createTempFile(fileName);
    }

    /**
     * congiftxgen
     */
    @Override
    public File generateGenesisBlock(String memberName, String mspId, File mspDirectory, String systemChainId,
                                     String consortium, ArrayList<String> ordererAddresses) {
        File config = new File(CONFIGTXGEN.getParent() + "configtx.yaml");
        synchronized (CONFIGTXGEN) {
            try {
                newConfigtxgenConfigHelper(memberName, mspId, mspDirectory).withConsortiumName(consortium)
                        .withOrdererAddressed(new HashSet<>(ordererAddresses))
                        .generateGenesisBlockConfig(config);
            } catch (Throwable e) {
                throw new FabricServiceException(CONFIGTXGEN_GENERATE_CONFIG_ERROR, e);
            }

            try {
                ConfigTxGen configTxGen = new ConfigTxGen(CONFIGTXGEN, config);
                File output = createTempOutputFile("genesis.block");
                return configTxGen.generateGenesisBlock(systemChainId, output);
            } catch (Throwable e) {
                throw new FabricServiceException(CONFIGTXGEN_CALL_ERROR, e);
            } finally {
                config.delete();
            }
        }
    }

    @Override
    public InputStream generateMemberConfig(String memberName, String mspId, File mspDirectory) {
        File config = new File(CONFIGTXGEN.getParent() + "configtx.yaml");
        synchronized (CONFIGTXGEN) {
            try {
                newConfigtxgenConfigHelper(memberName, mspId, mspDirectory).generateMemberConfigConfig(config);
            } catch (Throwable e) {
                throw new FabricServiceException(CONFIGTXGEN_GENERATE_CONFIG_ERROR, e);
            }

            try {
                ConfigTxGen configTxGen = new ConfigTxGen(CONFIGTXGEN, config);
                return configTxGen.generateMemberConfig(memberName);
            } catch (Throwable e) {
                throw new FabricServiceException(CONFIGTXGEN_CALL_ERROR, e);
            } finally {
                config.delete();
            }
        }
    }

    @Override
    public byte[] generateCreateChannelTx(String memberName, String mspId, File mspDirectory, String channelId, String consortium) {
        File config = new File(CONFIGTXGEN.getParent() + "configtx.yaml");
        synchronized (CONFIGTXGEN) {
            try {
                newConfigtxgenConfigHelper(memberName, mspId, mspDirectory)
                        .withConsortiumName(consortium).generateCreateChannelConfig(config);
            } catch (Throwable e) {
                throw new FabricServiceException(CONFIGTXGEN_GENERATE_CONFIG_ERROR, e);
            }

            try {
                ConfigTxGen configTxGen = new ConfigTxGen(CONFIGTXGEN, config);
                return configTxGen.generateCreateChannelTx(channelId);
            } catch (Throwable e) {
                throw new FabricServiceException(CONFIGTXGEN_CALL_ERROR, e);
            } finally {
                config.delete();
            }
        }
    }

    private ConfigtxgenConfigHelper newConfigtxgenConfigHelper(String memberName, String mspId, File mspDirectory) {
        try {
            return new ConfigtxgenConfigHelper(CONFIGTXGEN_CONFIG_TEMPLATE)
                    .withMember(memberName, mspId, mspDirectory);
        } catch (Throwable e) {
            throw new FabricServiceException(CONFIGTXGEN_GENERATE_CONFIG_ERROR, e);
        }
    }

    /**
     * configtxlator
     */
    @Override
    public byte[] encode(String data, String protoType) {
        try {
            ConfigTxLator configTxLator = new ConfigTxLator(CONFIGTXLATOR);
            return configTxLator.encode(data, transProto(protoType));
        } catch (Throwable e) {
            throw new FabricServiceException(CONFIGTXLATOR_CALL_ERROR, e);
        }
    }

    @Override
    public String decode(byte[] data, String protoType) {
        try {
            ConfigTxLator configTxLator = new ConfigTxLator(CONFIGTXLATOR);
            return configTxLator.decode(data, transProto(protoType));
        } catch (Throwable e) {
            throw new FabricServiceException(CONFIGTXLATOR_CALL_ERROR, e);
        }
    }

    @Override
    public byte[] computeUpdate(byte[] original, byte[] updated, String channelName) {
        try {
            ConfigTxLator configTxLator = new ConfigTxLator(CONFIGTXLATOR);
            return configTxLator.computeUpdate(original, updated, channelName);
        } catch (Throwable e) {
            throw new FabricServiceException(CONFIGTXLATOR_CALL_ERROR, e);
        }
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
    public void generateCrypto(String orgName, String identity, int peerCount, int userCount) {
        File config = new File(CONFIGTXGEN.getParent() + "crypto-config.yaml");
        CryptogenConfigHelper cryptogenConfigHelper = new CryptogenConfigHelper(CRYPTOGEN_CONFIG_TEMPLATE)
                .withOrgName(orgName)
                .withIdentity(identity)
                .withPeerCount(peerCount)
                .withUserCount(userCount);

        synchronized (CRYPTOGEN) {
            try {
                cryptogenConfigHelper.modifyConfigFile(config);
            } catch (Throwable e) {
                throw new FabricServiceException(CRYPTOGEN_GENERATE_CONFIG_ERROE, e);
            }

            try {
                Cryptogen cryptogen = new Cryptogen(CRYPTOGEN, config);
                cryptogen.extendCrypto();
            } catch (Throwable e) {
                throw new FabricServiceException(CRYPTOGEN_CALL_ERROR, e);
            } finally {
                config.delete();
            }
        }
    }

    @Override
    public void extendCrypto(String orgName, String identity, int peerCount, int userCount) {
        File config = new File(CONFIGTXGEN.getParent() + "crypto-config.yaml");
        CryptogenConfigHelper cryptogenConfigHelper = new CryptogenConfigHelper(CRYPTOGEN_CONFIG_TEMPLATE)
                .withOrgName(orgName)
                .withIdentity(identity)
                .withPeerCount(peerCount)
                .withUserCount(userCount);

        synchronized (CRYPTOGEN) {
            try {
                cryptogenConfigHelper.modifyConfigFile(config);
            } catch (Throwable e) {
                throw new FabricServiceException(CRYPTOGEN_GENERATE_CONFIG_ERROE, e);
            }

            try {
                Cryptogen cryptogen = new Cryptogen(CRYPTOGEN, config);
                cryptogen.generateCrypto();
            } catch (Throwable e) {
                throw new FabricServiceException(CRYPTOGEN_CALL_ERROR, e);
            } finally {
                config.delete();
            }
        }
    }
}
