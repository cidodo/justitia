package org.hyperledger.justitia.farbic.tools.helper;

import org.hyperledger.justitia.farbic.exception.FabricToolsConfigException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigtxgenConfigHelper extends ConfigHelper{
    private static final String DEFAULT_ORDERER_ORG_NAME = "SampleOrg";

    private final File configTemplate;
    private String memberName;
    private String mspId;
    private File mspDirectory;
    private String consortiumName;
    private Set<String> ordererAddresses;

    public ConfigtxgenConfigHelper(File configTemplate) {
        notNull(configTemplate, "Configuration template file is null.");
        this.configTemplate = configTemplate;
    }

    public ConfigtxgenConfigHelper withMember(String memberName, String mspId, File mspDirectory) throws FabricToolsConfigException {
        notEmpty(this.memberName, "Member name is empty.");
        this.memberName = memberName;
        notEmpty(this.mspId, "Msp id is empty.");
        this.mspId = mspId;
        notNull(mspDirectory, "Msp directory  is null.");
        if (!mspDirectory.exists()) {
            throw new FabricToolsConfigException(String.format("Msp directory %s does not exist.", mspDirectory.getPath()));
        }
        if (!mspDirectory.isDirectory()) {
            throw new FabricToolsConfigException(String.format("Msp directory %s is not a file directory.", mspDirectory.getPath()));
        }
        this.mspDirectory = mspDirectory;
        return this;
    }

    public ConfigtxgenConfigHelper withConsortiumName(String consortiumName) {
        this.consortiumName = consortiumName;
        return this;
    }

    public ConfigtxgenConfigHelper withOrdererAddressed(Set<String> ordererAddressed) {
        this.ordererAddresses = ordererAddressed;
        return this;
    }


    public void generateGenesisBlockConfig(File output) throws IOException, FabricToolsConfigException {
        notEmpty(this.consortiumName, "Consortium name is empty.");
        notEmpty(this.ordererAddresses, "Orderers node address is empty");

        Map original = readConfigFromYaml(configTemplate);
        setOrganizationInfo(original, this.memberName, this.mspId, this.mspDirectory);
        setConsortiumInfo(original, this.consortiumName);
        setOrdererAddressesInfo(original, this.ordererAddresses);
        writeConfigToYaml(original, output);
    }

    public void generateMemberConfigConfig(File output) throws IOException, FabricToolsConfigException {
        Map original = readConfigFromYaml(configTemplate);
        setOrganizationInfo(original, this.memberName, this.mspId, this.mspDirectory);
        setConsortiumInfo(original, this.consortiumName);
        setOrdererAddressesInfo(original, this.ordererAddresses);
        writeConfigToYaml(original, output);
    }

    public void generateCreateChannelConfig(File output) throws IOException, FabricToolsConfigException {
        notEmpty(this.consortiumName, "Consortium name is empty.");

        Map original = readConfigFromYaml(configTemplate);
        setOrganizationInfo(original, this.memberName, this.mspId, this.mspDirectory);
        setConsortiumInfo(original, this.consortiumName);
        writeConfigToYaml(original, output);
    }


    @SuppressWarnings("unchecked")
    private synchronized void setOrganizationInfo(Map config, String orgName, String mspId, File mspDir) throws FabricToolsConfigException {
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
                                if (org.containsKey("Name") && DEFAULT_ORDERER_ORG_NAME.equals(org.get("Name"))) {
                                    sampleOrg = org;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new FabricToolsConfigException(String.format("Profile %s data format error.", configTemplate.getPath()));
        }

        if (sampleOrg == null) {
            String msg= String.format("Missing organization %s information in Profile %s", DEFAULT_ORDERER_ORG_NAME, configTemplate.getPath());
            throw new FabricToolsConfigException(msg);
        }
        if (sampleOrg.containsKey("Name") && sampleOrg.containsKey("ID") && sampleOrg.containsKey("MSPDir")) {
            sampleOrg.replace("Name", orgName);
            sampleOrg.replace("ID", mspId);
            sampleOrg.replace("MSPDir", mspDir.getPath());
            if (sampleOrg.containsKey("Policies")) {
                sampleOrg.replace("Policies", generatePolicies(mspId));
            } else {
                sampleOrg.put("Policies", generatePolicies(mspId));
            }
        } else {
            String msg= String.format("Missing organization %s information in Profile %s", DEFAULT_ORDERER_ORG_NAME, configTemplate.getPath());
            throw new FabricToolsConfigException(msg);
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
    private synchronized void setConsortiumInfo(Map config, String consortiumName) throws FabricToolsConfigException {
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
            String msg = String.format("Profile %s data format error.", configTemplate.getPath());
            throw new FabricToolsConfigException(msg);
        }
    }

    @SuppressWarnings("unchecked")
    private void setOrdererAddressesInfo(Map original, Set<String> ordererAddresses) throws FabricToolsConfigException {
        if (ordererAddresses != null && !ordererAddresses.isEmpty()) {
            try {
                Map orderer = (Map) original.get("Orderer");
                orderer.replace("Addresses", ordererAddresses);
                ((Map) ((Map) ((Map) original.get("Profiles")).get("OrdererGenesis")).get("Orderer")).replace("Addresses", ordererAddresses);
            } catch (Throwable e) {
                String msg= String.format("Missing orderer information in Profile %s", configTemplate.getPath());
                throw new FabricToolsConfigException(msg);
            }
        }
    }
}
