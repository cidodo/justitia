package org.hyperledger.justitia.farbic.sdk;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.justitia.common.utils.StringUtils;
import org.hyperledger.justitia.farbic.exception.HFClientContextException;
import org.hyperledger.justitia.identity.service.beans.NodeInfo;
import org.hyperledger.justitia.identity.service.beans.OrdererInfo;
import org.hyperledger.justitia.identity.service.beans.PeerInfo;
import org.hyperledger.justitia.identity.service.beans.FabricUserInfo;
import org.hyperledger.justitia.identity.service.beans.crypto.MspInfo;
import org.hyperledger.justitia.identity.service.beans.crypto.TlsInfo;
import org.hyperledger.justitia.identity.service.read.NodeReader;
import org.hyperledger.justitia.identity.service.read.OrganizationReader;
import org.hyperledger.justitia.identity.service.read.UserReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


@Service
public class HFClientHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HFClientHelper.class);
    private static final String FABRIC_SDK_CONFIG_FILE = "resources/fabric/config/config.properties";
    static {
        System.setProperty(Config.ORG_HYPERLEDGER_FABRIC_SDK_CONFIGURATION, FABRIC_SDK_CONFIG_FILE);
    }

    private static HFClient hfClient = null;
    private final NodeReader nodeReader;
    private final OrganizationReader orgReader;
    private final UserReader userReader;


    @Autowired
    public HFClientHelper(NodeReader nodeReader, OrganizationReader orgReader, UserReader userReader) {
        this.nodeReader = nodeReader;
        this.orgReader = orgReader;
        this.userReader = userReader;
    }

    public HFClient getHFClient() throws HFClientContextException {
        if (HFClientHelper.hfClient == null) {
            synchronized (this) {
                if (HFClientHelper.hfClient == null) {
                    HFClientHelper.hfClient = createHFClient();
                }
            }
        }
        return HFClientHelper.hfClient;
    }



    public HFClient createHFClient() throws HFClientContextException {
        String mspId = orgReader.getMspId();
        //FIXME 默认是管理员用户
        FabricUserInfo adminUserInfo = userReader.getAdminUser();
        if (null == adminUserInfo || null == mspId || mspId.isEmpty()) {
            throw new HFClientContextException("Does not exist in the admin user.");
        }

        FabricUserImpl fabricUser = new FabricUserImpl(adminUserInfo.getId(), mspId);
        fabricUser.setMspId(mspId);
        try {
            MspInfo msp = adminUserInfo.getCrypto().getMspInfo();
            fabricUser.setEnrollment(msp.getSignCerts(), msp.getKeyStore());
        } catch (IOException e) {
            throw new HFClientContextException("User certificate read failed.", e);
        }

        HFClient client = HFClient.createNewInstance();
        try {
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            client.setUserContext(fabricUser);
        } catch (Exception e) {
            throw new HFClientContextException("Fabric client identity configuration failed.", e);
        }
        return client;
    }

    public List<Peer> createPeers(Collection<String> peersId) throws HFClientContextException {
        if (null == peersId) {
            return null;
        }
        List<Peer> peers = new ArrayList<>();
        for (String peerId : peersId) {
            try {
                peers.add(createPeer(peerId));
            } catch (InvalidArgumentException e) {
                LOGGER.warn("Create peer object failed.",e);
            }
        }
        return peers;
    }

    public Peer createPeer(String peerId) throws InvalidArgumentException, HFClientContextException {
        PeerInfo peerInfo = nodeReader.getPeerInfoWithTls(peerId);
        return createNode(peerInfo, Peer.class);
    }

    public List<Orderer> createOrderers(Collection<String> orderersId) throws HFClientContextException {
        List<Orderer> orderers = new ArrayList<>();
        if (null == orderersId) {
            return null;
        }
        for (String ordererId : orderersId) {
            try {
                orderers.add(createOrderer(ordererId));
            } catch (InvalidArgumentException e) {
                LOGGER.warn("Create orderer object failed.",e);
            }
        }
        return orderers;
    }

    public Orderer createOrderer(String ordererId) throws InvalidArgumentException, HFClientContextException {
        OrdererInfo ordererInfo = nodeReader.getOrdererInfoWithTls(ordererId);
        return createNode(ordererInfo, Orderer.class);
    }

    public Orderer createOrderer() {
        List<Orderer> orderers = createOrderers();
        if (orderers == null || orderers.isEmpty()) {
            LOGGER.warn("Not found any orderer node information.");
            return null;
        }
        return orderers.get(0);
    }

    List<Orderer> createOrderers() {
        List<OrdererInfo> orderersInfo = nodeReader.getOrderersInfoWithTls();
        List<Orderer> orderers = new ArrayList<>();
        for (OrdererInfo ordererInfo : orderersInfo) {
            try {
                orderers.add(createNode(ordererInfo, Orderer.class));
            } catch (Exception e) {
                LOGGER.warn("Create orderer object failed.",e);
            }
        }
        return orderers;
    }

    @SuppressWarnings("unchecked")
    private <T> T createNode(NodeInfo nodeInfo, Class<T> nodeType) throws InvalidArgumentException, HFClientContextException {
        if (nodeInfo == null) {
            return null;
        }

        Properties properties = new Properties();
        if (nodeInfo.getTlsEnable()) {
            TlsInfo tls = nodeInfo.getCrypto().getTlsInfo();
            String ca = tls.getCa();
            if (StringUtils.isNotEmpty(ca)) {
                properties.put("pemBytes", ca.getBytes());
                if (nodeInfo.getDoubleVerity()) {
                    //如果peer节点没有开启TLS双端认证这个配置就不能给，不然TLS握手失败
                    properties.put("clientCertBytes", tls.getCert().getBytes());
                    properties.put("clientKeyBytes", tls.getKey().getBytes());
                }

                properties.setProperty("sslProvider", "openSSL");
                properties.setProperty("negotiationType", "TLS");

                //信任服务端证书
                properties.setProperty("trustServerCertificate", "true");
            }
        }

        if (Peer.class == nodeType) {
            return (T) getHFClient().newPeer(nodeInfo.getId(), nodeInfo.getUrl(), properties);
        } else if (Orderer.class == nodeType) {
            return (T) getHFClient().newOrderer(nodeInfo.getId(), nodeInfo.getUrl(), properties);
        } else {
            return null;
        }
    }
}
