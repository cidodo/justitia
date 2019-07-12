package org.hyperledger.justitia.farbic.sdk;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.justitia.common.face.modules.identity.beans.FabricUser;
import org.hyperledger.justitia.common.utils.CheckUtils;

public class NetworkConfig {
    private static final Log logger = LogFactory.getLog(NetworkConfig.class);
    private static NetworkConfig instance;

    private OrgInfo clientOrganization;
    private Map<String, Node> orderers;
    private Map<String, Node> peers;

    private NetworkConfig() {
    }

    public NetworkConfig getInstance() {
        if (null == instance) {
            synchronized (NetworkConfig.class) {
                instance = new NetworkConfig();
            }
        }
        return instance;
    }

    public OrgInfo getClientOrganization() {
        return clientOrganization;
    }

    public void setClientOrganization(String name, String mspId) {
        this.clientOrganization = new OrgInfo(name, mspId);
    }

    public void setPeerAdmin(FabricUser admin) {
        this.clientOrganization.setPeerAdmin(admin);
    }

    public Map<String, Node> getOrderers() {
        return orderers;
    }

    public void setOrderers(Map<String, Node> orderers) {
        this.orderers = orderers;
    }

    public Map<String, Node> getPeers() {
        return peers;
    }

    public void setPeers(Map<String, Node> peers) {
        this.peers = peers;
    }

    /**
     * Holds a network "node" (eg. Peer, Orderer)
     */
    private class Node {
        private final String name;
        private final String url;
        private Properties properties;

        private Node(String name, String url, Properties properties) {
            this.url = url;
            this.name = name;
            this.properties = properties;
        }

        private String getName() {
            return name;
        }

        private String getUrl() {
            return url;
        }

        private Properties getProperties() {
            return properties;
        }
    }

    /**
     * Holds details of an Organization
     */
    public static class OrgInfo {
        private final String name;
        private final String mspId;
        private final List<String> peerNames = new ArrayList<>();
        private final List<CAInfo> certificateAuthorities = new ArrayList<>();
        private Map<String, FabricUser> users = new HashMap<>();
        private FabricUser peerAdmin;

        OrgInfo(String orgName, String mspId) {
            this.name = orgName;
            this.mspId = mspId;
        }

        private void addPeerName(String peerName) {
            peerNames.add(peerName);
        }

        private void addCertificateAuthority(CAInfo ca) {
            certificateAuthorities.add(ca);
        }

        public String getName() {
            return name;
        }

        public String getMspId() {
            return mspId;
        }

        public List<String> getPeerNames() {
            return new LinkedList<>(peerNames);
        }

        public List<CAInfo> getCertificateAuthorities() {
            return new LinkedList<>(certificateAuthorities);
        }

        public void addUser(FabricUser user) {
            CheckUtils.notNull(user, "User is null.");
            this.users.put(user.getName(), user);
        }

        public FabricUser getUser(String userName) {
            CheckUtils.notEmpty(userName, "User's name is empty.");
            return users.get(userName);
        }

        public FabricUser removeUser(String userName) {
            CheckUtils.notEmpty(userName, "User's name is empty.");
            if (this.users.containsKey(userName)) {
                return users.remove(userName);
            } else {
                return null;
            }
        }

        public FabricUser getPeerAdmin() {
            return peerAdmin;
        }

        public void setPeerAdmin(FabricUser peerAdmin) {
            this.peerAdmin = peerAdmin;
        }
    }

    /**
     * Holds the details of a Certificate Authority
     */
    public static class CAInfo {
        private final String name;
        private final String url;
        private final Properties httpOptions;
        private final String mspid;
        private String caName;
        private Properties properties;

        private final List<FabricUser> registrars;

        CAInfo(String name, String mspid, String url, List<FabricUser> registrars, Properties httpOptions) {
            this.name = name;
            this.url = url;
            this.httpOptions = httpOptions;
            this.registrars = registrars;
            this.mspid = mspid;
        }

        private void setCaName(String caName) {
            this.caName = caName;
        }

        public String getName() {
            return name;
        }

        public String getCAName() {
            return caName;
        }

        public String getUrl() {
            return url;
        }

        public Properties getHttpOptions() {
            return httpOptions;
        }

        void setProperties(Properties properties) {
            this.properties = properties;
        }

        public Properties getProperties() {
            return this.properties;
        }

        public Collection<FabricUser> getRegistrars() {
            return new LinkedList<>(registrars);
        }
    }

}
