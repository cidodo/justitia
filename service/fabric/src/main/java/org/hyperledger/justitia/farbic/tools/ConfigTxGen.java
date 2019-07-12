package org.hyperledger.justitia.farbic.tools;

import org.apache.commons.io.IOUtils;
import org.hyperledger.justitia.common.ExternalResources;
import org.hyperledger.justitia.common.utils.file.file.FileUtils;
import org.hyperledger.justitia.common.utils.file.file.YamlFileUtils;
import org.hyperledger.justitia.farbic.exception.FabricToolsException;
import org.hyperledger.justitia.common.utils.StringUtils;
import org.hyperledger.justitia.farbic.utils.ssh.CallLocalShell;
import org.hyperledger.justitia.farbic.utils.ssh.CallShell;
import org.hyperledger.justitia.identity.exception.MspException;
import org.hyperledger.justitia.common.face.modules.identity.beans.OrganizationInfo;
import org.hyperledger.justitia.common.face.modules.identity.msp.MspService;
import org.hyperledger.justitia.common.face.modules.identity.read.OrganizationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigTxGen {
    private static final String DEFAULT_ORDERER_ORG_NAME = "SampleOrg";
    private static final String CONFIG_TX_TEMPLATE_FILE = "configtx_template.yaml";


    private final MspService mspService;
    private final OrganizationReader organizationReader;

    @Autowired
    public ConfigTxGen(MspService mspService, OrganizationReader organizationReader) {
        this.mspService = mspService;
        this.organizationReader = organizationReader;
    }

    //------------------------------------------------ genesis block ---------------------------------------------------
    public File createGenesisBlock(String systemChainId, String consortium, ArrayList<String> ordererAddresses) throws FabricToolsException {
        File genesisBlockFile = createOutputFile("genesis.block");
        String command = String.format("./configtxgen -profile OrdererGenesis -channelID %s -outputBlock %s", systemChainId, genesisBlockFile.getPath());
        CallShell.Result result = callConfigTxGen(command, consortium, ordererAddresses);

        if (result.isSuccess()) {
            return genesisBlockFile;
        } else {
            throw new FabricToolsException("Configtxgen call failed:" + result.getErrorInfo());
        }
    }

    //---------------------------------------------- organization config -----------------------------------------------
    public InputStream createOrgConfig() throws FabricToolsException {
        String organizationName = organizationReader.getName();
        String command = String.format("./configtxgen -printOrg %s", organizationName);
        CallShell.Result result = callConfigTxGen(command, null, null);
        if (result.isSuccess()) {
            return new ByteArrayInputStream(result.getPrintInfo().getBytes());
        } else {
            throw new FabricToolsException("Configtxgen call failed:" + result.getErrorInfo());
        }
    }

    //--------------------------------------------- create channel tx --------------------------------------------------
    public byte[] createChannelTx(String channelId, String consortium) throws FabricToolsException {
        File outputFile = createOutputFile("channel.tx");
        try {
            String command;
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                command = String.format("configtxgen.exe -profile OrgsChannel -outputCreateChannelTx %s -channelID %s", outputFile.getPath(), channelId);
            } else {
                command = String.format("./configtxgen -profile OrgsChannel -outputCreateChannelTx %s -channelID %s", outputFile.getPath(), channelId);
            }
            CallShell.Result result = callConfigTxGen(command, consortium, null);

            if (result.isSuccess()) {
                try {
                    FileInputStream is = new FileInputStream(outputFile);
                    return IOUtils.toByteArray(is);
                } catch (IOException e) {
                    String msg = String.format("Channel configuration file %s read failed.", outputFile.getPath());
                    throw new FabricToolsException(msg, e);
                }
            } else {
                throw new FabricToolsException("Configtxgen call failed:" + result.getErrorInfo());
            }
        } finally {
            FileUtils.delete(outputFile.getParent());
        }
    }

    //-------------------------------------------------- common --------------------------------------------------------

    private File createOutputFile(String fileName) throws FabricToolsException {
        String output = ExternalResources.getUniqueTempDir() + File.separator + fileName;
        File outputFile = new File(output);
        try {
            FileUtils.makeDir(outputFile.getParent());
            if (!outputFile.createNewFile()) {
                String msg = String.format("Configtxgen output file %s create failed.", output);
                throw new FabricToolsException(msg);
            }
        } catch (IOException e) {
            String msg = String.format("Configtxgen output file %s create failed.", output);
            throw new FabricToolsException(msg, e);
        }
        return outputFile;
    }

    private synchronized CallShell.Result callConfigTxGen(String command, String consortium, ArrayList<String> ordererAddresses) throws FabricToolsException {
        //修改配置文件
        OrganizationInfo organizationInfo = organizationReader.getOrganizationInfo();
        String tempDir = ExternalResources.getUniqueTempDir();
        String mspDir;
        try {
            mspDir = mspService.generateOrgMsp(tempDir, organizationInfo.getId()).getPath();
        } catch (IOException | MspException e) {
            throw new FabricToolsException("Organization msp folder generate failed", e);
        }
        String configTxFile = modifyConfigFile(organizationInfo.getName(), organizationInfo.getMspId(), mspDir, consortium, ordererAddresses);
        try {
            //检查configtxgen工具是否存在
            String configtxgen = ExternalResources.getScripts("tools/configtxgen");
            File configtxgenFile = new File(configtxgen);
            if (!configtxgenFile.exists()) {
                throw new FabricToolsException("No such file " + configtxgen);
            }
            CallLocalShell shell = new CallLocalShell();
            return shell.execCmd(command, configtxgenFile.getParentFile());
        } catch (InterruptedException | IOException e) {
            throw new FabricToolsException("Configtxgen tool call failed.", e);
        } finally {
            FileUtils.delete(tempDir);
            FileUtils.delete(configTxFile);
        }
    }

    //FIXME 专为不动产写的
    public synchronized CallShell.Result callConfigTxGen(String command, String orgName, String mspId, File msp, String consortium, ArrayList<String> ordererAddresses) throws FabricToolsException {
        String configTxFile = modifyConfigFile(orgName, mspId, msp.getPath(), consortium, ordererAddresses);
        try {
            //检查configtxgen工具是否存在
            String configtxgen = ExternalResources.getScripts("tools/configtxgen");
            File configtxgenFile = new File(configtxgen);
            if (!configtxgenFile.exists()) {
                throw new FabricToolsException("No such file " + configtxgen);
            }
            CallLocalShell shell = new CallLocalShell();
            return shell.execCmd(command, configtxgenFile.getParentFile());
        } catch (InterruptedException | IOException e) {
            throw new FabricToolsException("Configtxgen tool call failed.", e);
        } finally {
            FileUtils.delete(configTxFile);
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized String modifyConfigFile(String orgName, String mspId, String mspDir, String consortiumName,
                                                 ArrayList<String> ordererAddresses) throws FabricToolsException {

        String configTxTempFile = ExternalResources.getScripts("tools/" + CONFIG_TX_TEMPLATE_FILE);
        YamlFileUtils yamlUtils = new YamlFileUtils();
        Map configTxMap;
        try {
            configTxMap = yamlUtils.readYamlFileAsMap(configTxTempFile);
        } catch (FileNotFoundException e) {
            String msg = String.format("Not found config template file %s.",configTxTempFile);
            throw new FabricToolsException(msg);
        }
        if (configTxMap == null) {
            String msg = String.format("Configuration template %s read failed.",configTxTempFile);
            throw new FabricToolsException(msg);
        }
        //设置组织信息
        setOrganizationInfo(configTxMap, orgName, mspId, mspDir);
        //设置联盟信息
        if (StringUtils.isNotEmpty(consortiumName)) {
            setConsortiumInfo(configTxMap, consortiumName);
        }
        //set orderers address
        if (ordererAddresses != null && !ordererAddresses.isEmpty()) {
            try {
                Map orderer = (Map) configTxMap.get("Orderer");
                orderer.replace("Addresses", ordererAddresses);
                ((Map) ((Map) ((Map) configTxMap.get("Profiles")).get("OrdererGenesis")).get("Orderer")).replace("Addresses", ordererAddresses);
            } catch (Exception e) {
                String msg= String.format("Missing orderer information in Profile %s", CONFIG_TX_TEMPLATE_FILE);
                throw new FabricToolsException(msg);
            }
        }

        //写入新的配置文件
        String configTxFile = ExternalResources.getScripts("tools/configtx.yaml");
        try {
            yamlUtils.writeYamlFile(configTxMap, configTxFile);
        } catch (IOException e) {
            throw new FabricToolsException("Failed to write configuration file.", e);
        }
        return configTxFile;
    }

    @SuppressWarnings("unchecked")
    private synchronized void setOrganizationInfo(Map config, String orgName, String mspId, String mspDir) throws FabricToolsException {
        Map sampleOrg = null;
        try {
            if (config != null && config.containsKey("Organizations")) {
                Object organizations = config.get("Organizations");
                if (organizations instanceof List) {
                    ArrayList organizationList = (ArrayList) organizations;
                    if (!organizationList.isEmpty()) {
                        for (Object organization : organizationList) {
                            if (organization == null) {
                                continue;
                            }
                            if (organization instanceof Map) {
                                Map org = (Map) organization;
                                if (org.containsKey("Name") && ConfigTxGen.DEFAULT_ORDERER_ORG_NAME.equals(org.get("Name"))) {
                                    sampleOrg = org;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new FabricToolsException(String.format("Profile %s data format error.", CONFIG_TX_TEMPLATE_FILE));
        }

        if (sampleOrg == null) {
            String msg= String.format("Missing organization %s information in Profile %s", DEFAULT_ORDERER_ORG_NAME, CONFIG_TX_TEMPLATE_FILE);
            throw new FabricToolsException(msg);
        }
        if (sampleOrg.containsKey("Name") && sampleOrg.containsKey("ID") && sampleOrg.containsKey("MSPDir")) {
            sampleOrg.replace("Name", orgName);
            sampleOrg.replace("ID", mspId);
            sampleOrg.replace("MSPDir", mspDir);
            if (sampleOrg.containsKey("Policies")) {
                sampleOrg.replace("Policies", generatePolicies(mspId));
            } else {
                sampleOrg.put("Policies", generatePolicies(mspId));
            }
        } else {
            String msg= String.format("Missing organization %s information in Profile %s", DEFAULT_ORDERER_ORG_NAME, CONFIG_TX_TEMPLATE_FILE);
            throw new FabricToolsException(msg);
        }
    }

    private Map generatePolicies(String mspId) {
        Map<String, String> readers = new LinkedHashMap<>();
        String readerRule = "OR('" + mspId + ".member')";
        readers.put("Type", "Signature");
        readers.put("Rule", readerRule);

        Map<String, String> writers = new LinkedHashMap<>();
        String writerRule = "OR('" + mspId + ".member')";
        writers.put("Type", "Signature");
        writers.put("Rule", writerRule);

        Map<String, String> admins = new LinkedHashMap<>();
        String adminRule = "OR('" + mspId + ".admin')";
        admins.put("Type", "Signature");
        admins.put("Rule", adminRule);

        Map<String, Map> policies = new LinkedHashMap<>();
        policies.put("Readers", readers);
        policies.put("Writers", writers);
        policies.put("Admins", admins);
        return policies;
    }

    @SuppressWarnings("unchecked")
    private synchronized void setConsortiumInfo(Map config, String consortiumName) throws FabricToolsException {
        try {
            if (config.containsKey("Profiles")) {
                Map profiles = (Map) config.get("Profiles");
                if (profiles != null && profiles.containsKey("OrdererGenesis")) {
                    Map ordererGenesis = (Map) profiles.get("OrdererGenesis");
                    if (ordererGenesis != null && ordererGenesis.containsKey("Consortiums")) {
                        Map consortiums = (Map) ordererGenesis.get("Consortiums");
                        if (consortiums != null && consortiums.containsKey("SampleConsortium")) {
                            Map sampleConsortium = (Map) consortiums.remove("SampleConsortium");
                            consortiums.put(consortiumName, sampleConsortium);
                        }
                    }
                }

                if (profiles != null && profiles.containsKey("OrgsChannel")) {
                    Map orgsChannel = (Map) profiles.get("OrgsChannel");
                    if (orgsChannel != null && orgsChannel.containsKey("Consortium")) {
                        orgsChannel.replace("Consortium", consortiumName);
                    }
                }
            }
        } catch (RuntimeException e) {
            String msg = String.format("Profile %s data format error.", CONFIG_TX_TEMPLATE_FILE);
            throw new FabricToolsException(msg);
        }
    }
}
