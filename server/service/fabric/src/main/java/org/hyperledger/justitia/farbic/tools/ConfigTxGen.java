package org.hyperledger.justitia.farbic.tools;

import org.apache.commons.io.IOUtils;
import org.hyperledger.justitia.common.utils.file.file.FileUtils;
import org.hyperledger.justitia.farbic.exception.FabricToolsConfigException;
import org.hyperledger.justitia.farbic.exception.FabricToolsException;
import org.hyperledger.justitia.farbic.utils.ssh.CallLocalShell;
import org.hyperledger.justitia.farbic.utils.ssh.CallShell;

import java.io.*;
public class ConfigTxGen {
    private final File configtxgen;
    private final File config;

    public ConfigTxGen(File configtxgen, File config) throws FabricToolsException {
        if (!configtxgen.exists()) {
            throw new FabricToolsException(String.format("Not found fabric tool configtxgen in directory %s.", configtxgen.getPath()));
        }
        this.configtxgen = configtxgen;

        if (!config.exists()) {
            throw new FabricToolsConfigException(String.format("Not found configtxgen's config file in directory %s.", config.getPath()));
        }
        this.config = config;
    }

    //------------------------------------------------ genesis block ---------------------------------------------------
    public File generateGenesisBlock(String systemChainId, File output) throws FabricToolsException {
        String command = newCommandBuilder()
                .withProfile("OrdererGenesis")
                .withChannelId(systemChainId)
                .withOutputBlock(output.getPath())
                .build();
        CallShell.Result result = callConfigTxGen(command);
        if (result.isSuccess()) {
            return output;
        } else {
            throw new FabricToolsException("Configtxgen call failed:" + result.getErrorInfo());
        }
    }

    //---------------------------------------------- organization config -----------------------------------------------
    public InputStream generateMemberConfig(String organizationName) throws FabricToolsException {
        String command = newCommandBuilder()
                .withPrintOrg(organizationName)
                .build();
        CallShell.Result result = callConfigTxGen(command);
        if (result.isSuccess()) {
            return new ByteArrayInputStream(result.getPrintInfo().getBytes());
        } else {
            throw new FabricToolsException("Configtxgen call failed:" + result.getErrorInfo());
        }
    }

    //--------------------------------------------- create channel tx --------------------------------------------------
    public byte[] generateCreateChannelTx(String channelId) throws FabricToolsException, IOException {
        File output = FileUtils.createTempFile("channel.tx");
        try {
            String command = newCommandBuilder()
                    .withProfile("OrgsChannel")
                    .withOutputCreateChannelTx(output.getPath())
                    .withChannelId(channelId)
                    .build();

            CallShell.Result result = callConfigTxGen(command);

            if (result.isSuccess()) {
                try {
                    FileInputStream is = new FileInputStream(output);
                    return IOUtils.toByteArray(is);
                } catch (IOException e) {
                    String msg = String.format("Channel configuration file %s read failed.", output.getPath());
                    throw new FabricToolsException(msg, e);
                }
            } else {
                throw new FabricToolsException("Configtxgen call failed:" + result.getErrorInfo());
            }
        }finally {
            FileUtils.delete(output.getParent());
        }
    }

    private CallShell.Result callConfigTxGen(String command) throws FabricToolsException {
        try {
            CallLocalShell shell = new CallLocalShell();
            synchronized (ConfigTxGen.class) {
                return shell.execCmd(command, configtxgen.getParentFile());
            }
        } catch (InterruptedException | IOException e) {
            throw new FabricToolsException("Configtxgen tool call failed.", e);
        }
    }

    private CommandBuilder newCommandBuilder() {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            return new CommandBuilder("configtxgen.exe");
        } else {
            return new CommandBuilder("./configtxgen");
        }
    }

    private class CommandBuilder {
        private StringBuilder command;

        CommandBuilder(String toolPath) {
            this.command = new StringBuilder(toolPath);
        }

        CommandBuilder withAsOrg(String asOrg) {
            command.append(" ").append("-asOrg").append(" ").append(asOrg);
            return this;
        }

        CommandBuilder withChannelId(String channelId) {
            command.append(" ").append("-channelID").append(" ").append(channelId);
            return this;
        }

        CommandBuilder withConfigPath(String configPath) {
            command.append(" ").append("-configPath").append(" ").append(configPath);
            return this;
        }

        CommandBuilder withInspectBlock(String inspectBlock) {
            command.append(" ").append("-inspectBlock").append(" ").append(inspectBlock);
            return this;
        }

        CommandBuilder withInspectChannelCreateTx(String inspectChannelCreateTx) {
            command.append(" ").append("-inspectChannelCreateTx").append(" ").append(inspectChannelCreateTx);
            return this;
        }

        CommandBuilder withOutputAnchorPeersUpdate(String outputAnchorPeersUpdate) {
            command.append(" ").append("-outputAnchorPeersUpdate").append(" ").append(outputAnchorPeersUpdate);
            return this;
        }

        CommandBuilder withOutputBlock(String outputBlock) {
            command.append(" ").append("-outputBlock").append(" ").append(outputBlock);
            return this;
        }

        CommandBuilder withOutputCreateChannelTx(String outputCreateChannelTx) {
            command.append(" ").append("-outputCreateChannelTx").append(" ").append(outputCreateChannelTx);
            return this;
        }

        CommandBuilder withPrintOrg(String printOrg) {
            command.append(" ").append("-printOrg").append(" ").append(printOrg);
            return this;
        }

        CommandBuilder withProfile(String profile) {
            command.append(" ").append("-profile").append(" ").append(profile);
            return this;
        }

        String build() {
            return command.toString();
        }
    }
}
