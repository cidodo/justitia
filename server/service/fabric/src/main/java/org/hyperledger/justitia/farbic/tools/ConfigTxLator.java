package org.hyperledger.justitia.farbic.tools;

import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.justitia.common.utils.file.FileUtils;
import org.hyperledger.justitia.farbic.exception.FabricToolsException;
import org.hyperledger.justitia.farbic.tools.helper.ComputeUpdate;
import org.hyperledger.justitia.farbic.utils.ssh.CallLocalShell;
import org.hyperledger.justitia.farbic.utils.ssh.CallShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ConfigTxLator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigTxLator.class);
    private File configtxlator;

    public enum ProtoType {
        BLOCK("Block"),
        ENVELOPE("Envelope"),
        CONFIG("Config"),
        CONFIG_UPDATE("ConfigUpdate"),
        CONFIG_ENVELOPE("ConfigEnvelope"),
        CONFIG_UPDATE_ENVELOPE("ConfigUpdateEnvelope");

        private String type;
        ProtoType(String protoType) {
            this.type = protoType;
        }
        public String getType() {
            return type;
        }
    }

    ConfigTxLator(File configtxlator) throws FabricToolsException {
        if (null == configtxlator) {
            throw new IllegalArgumentException("Fabric tool configtxlator is null.");
        }

        if (!configtxlator.exists()) {
            throw new FabricToolsException(String.format("Not found fabric tool configtxlator in directory %s.", configtxlator.getPath()));
        }

        this.configtxlator = configtxlator;
    }

    public byte[] encode(String data, ProtoType protoType) throws IOException, FabricToolsException {
        File input = strToFile(data);
        File output = createTempOutputFile("Encode-" + protoType.getType() + ".pb");

        try {
            String command = String.format("./configtxlator proto_encode --type=common.%s --input=%s --output=%s",
                    protoType.getType(), input.getPath(), output.getPath());
            CallShell.Result result = callConfigTxLator(command);
            if (!result.isSuccess()) {
                throw new FabricToolsException("Encode string failed:" + result.getErrorInfo());
            }
            if (!output.exists()) {
                throw new FabricToolsException("Output stream redirection to file failed");
            }
            return fileToBytes(output);
        } finally {
            if (!input.delete()) {
                LOGGER.warn("File " + input.getPath() + " delete filed.");
            }
            if (!output.delete()) {
                LOGGER.warn("File " + output.getPath() + " delete filed.");
            }
        }
    }

    public String decode(byte[] data, ProtoType protoType) throws IOException, FabricToolsException {
        File input = bytesToFile(data);
        File output = createTempOutputFile("Decode-" + protoType.getType() + ".json");

        try {
            String command = String.format("./configtxlator proto_decode --type=common.%s --input=%s --output=%s",
                    protoType.getType(), input.getPath(), output.getPath());
            CallShell.Result result = callConfigTxLator(command);
            if (!result.isSuccess()) {
                throw new FabricToolsException("Decode bytes failed:" + result.getErrorInfo());
            }
            if (!output.exists()) {
                throw new FabricToolsException("Output stream redirection to file failed.");
            }
            return fileToStr(output);
        } finally {
            if (!input.delete()) {
                LOGGER.warn("File " + input.getPath() + " delete filed.");
            }
            if (!output.delete()) {
                LOGGER.warn("File " + output.getPath() + " delete filed.");
            }
        }
    }

    public byte[] computeUpdate(byte[] original, byte[] updated, String channelName) throws IOException, FabricToolsException {
        File originalFile = bytesToFile(original);
        File updatedFile = bytesToFile(updated);
        File output = createTempOutputFile("updatedConfig.pb");

        try {
            String command = String.format("./configtxlator compute_update --original=%s --updated=%s --channel_id=%s --output=%s",
                    originalFile.getPath(), updatedFile.getPath(), channelName, output);
            CallShell.Result result = callConfigTxLator(command);
            if (!result.isSuccess()) {
                throw new FabricToolsException("Compute update failed:" + result.getErrorInfo());
            }
            if (!output.exists()) {
                throw new FabricToolsException("Output stream redirection to file failed.");
            }
            return fileToBytes(output);
        } finally {
            if (!originalFile.delete()) {
                LOGGER.warn("File " + originalFile.getPath() + " delete filed.");
            }
            if (!updatedFile.delete()) {
                LOGGER.warn("File " + updatedFile.getPath() + " delete filed.");
            }
            if (!output.delete()) {
                LOGGER.warn("File " + output.getPath() + " delete filed.");
            }
        }

    }

    @Deprecated
    public byte[] computeUpdate(Configtx.Config original, Configtx.Config updated, String channelName) throws  FabricToolsException {
        ComputeUpdate computeUpdate = new ComputeUpdate();
        return computeUpdate.Compute(original, updated, channelName).toByteArray();
    }

    private CallShell.Result callConfigTxLator(String command) throws FabricToolsException {
        try {
            CallLocalShell shell = new CallLocalShell();
            synchronized (ConfigTxLator.class) {
                return shell.execCmd(command, configtxlator.getParentFile());
            }
        }catch (InterruptedException | IOException e) {
            throw new FabricToolsException("Configtxlator tool call failed.", e);
        }
    }

    private File createTempOutputFile(String fileName) throws IOException {
        return FileUtils.createTempFile(fileName);
    }

    private File strToFile(String data) throws FabricToolsException, IOException {
        String fileName = "" + System.currentTimeMillis() + (int) (Math.random() * 100) + ".json";
        File file = createTempOutputFile(fileName);
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(data);
            fileWriter.flush();
            return file;
        } catch (IOException e) {
            throw new FabricToolsException("File " + file.getPath() + " write failed",e);
        }
    }

    private String fileToStr(File file) throws FabricToolsException {
        byte[] bytes = fileToBytes(file);
        return new String(bytes);
    }

    private File bytesToFile(byte[] bytes) throws FabricToolsException, IOException {
        File file = createTempOutputFile("pb");
        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(bytes);
        } catch (IOException e) {
            throw new FabricToolsException("File " + file.getPath() + " write failed", e);
        }
        return file;
    }

    private byte[] fileToBytes(File file) throws FabricToolsException {
        if (!file.exists()) {
            throw new FabricToolsException("No such file " + file.getPath());
        }

        try (FileInputStream is = new FileInputStream(file)) {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new FabricToolsException("File " + file.getPath() + " read failed", e);
        }
    }
}
