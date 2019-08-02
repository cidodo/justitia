package org.hyperledger.justitia.farbic.tools.helper;

import org.hyperledger.justitia.farbic.exception.FabricToolsConfigException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptogenConfigHelper extends ConfigHelper{
    private final File configTemplate;
    private String orgName;
    private String identity;
    private int userCount;
    private int peerCount;

    public CryptogenConfigHelper(File configTemplate) {
        this.configTemplate = configTemplate;
    }

    public CryptogenConfigHelper withOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public CryptogenConfigHelper withIdentity(String identity) {
        this.identity = identity;
        return this;
    }

    public CryptogenConfigHelper withUserCount(int userCount) {
        this.userCount = userCount;
        return this;
    }

    public CryptogenConfigHelper withPeerCount(int peerCount) {
        this.peerCount = peerCount;
        return this;
    }

    public void modifyConfigFile(File output) throws IOException, FabricToolsConfigException {
        notEmpty(orgName, "Organization name is empty.");
        notEmpty(identity, "Organization identity is empty.");

        Map original = readConfigFromYaml(configTemplate);
        Map orgConfig = generatePeerOrgConfig(orgName, identity, userCount, peerCount);
        setPeerOrg(original, orgConfig);
        writeConfigToYaml(original, output);
    }

    @SuppressWarnings("unchecked")
    private void setPeerOrg(Map config, Map peerOrgConfig) {
        if (config.containsKey("PeerOrgs")) {
            List<Map> peerOrgs = (List<Map>) config.get("PeerOrgs");
            for (int i = 0; i < peerOrgs.size(); i++) {
                if (orgName.equals(peerOrgs.get(i).get("Name"))) {
                    peerOrgs.remove(i);
                }
            }
            peerOrgs.add(peerOrgConfig);
        }
    }

    private Map generatePeerOrgConfig(String orgName, String identity, int userCount, int peerCount) {
        ArrayList<Map> users = new ArrayList<>();
        if (userCount <= 0) {
            userCount = 3;
        }
        for (int i = 0; i < userCount; i++) {
            HashMap<String, String> identityMap = new HashMap<>();
            identityMap.put("Identify", identity);
            users.add(identityMap);
        }

        if (peerCount <= 0) {
            peerCount = 3;
        }
        HashMap<String, Integer> template = new HashMap<>();
        template.put("Count", peerCount);

        HashMap<String, String> ca = new HashMap<>();
        ca.put("Hostname", "ca");
        ca.put("Country", "CHN");
        ca.put("Province", "Jiangsu");
        ca.put("Locality", "Suzhou");
        ca.put("OrganizationalUnit", orgName + " BDC");

        HashMap<String, Object> peerOrg = new HashMap<>();
        peerOrg.put("Name", orgName);
        peerOrg.put("Domain", orgName.toLowerCase() + ".suzhou.com");
        peerOrg.put("CA", ca);
        peerOrg.put("EnableNodeOUs", false);
        peerOrg.put("Template", template);
        peerOrg.put("Users", users);

        return peerOrg;
    }
}
