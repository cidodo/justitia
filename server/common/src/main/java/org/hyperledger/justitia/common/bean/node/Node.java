package org.hyperledger.justitia.common.bean.node;

import lombok.Data;
import org.hyperledger.justitia.common.bean.identity.crypto.Crypto;
import org.hyperledger.justitia.common.bean.node.Container;
import org.hyperledger.justitia.common.bean.identity.crypto.Msp;
import org.hyperledger.justitia.common.utils.StringUtils;

import java.util.*;

@Data
public abstract class Node extends Crypto{
    //base info
    private String id;
    private String organizationId;
    private String ip;
    private Integer port;
    private Container container;
    //TLS
    private Boolean tlsEnable;
    private Boolean doubleVerity;
    private List<String> hostsName;
    //crypto
    private Properties properties;

    public enum NodeType {
        PEER("peer"),
        ORDERER("orderer"),
        COUCHDB("couchdb");

        private final static Map<String, NodeType> ENUM_MAP = new HashMap<>();

        static {
            for (NodeType v : values()) {
                ENUM_MAP.put(v.getOper(), v);
            }
        }

        public static NodeType fromString(String oper) {
            return ENUM_MAP.get(oper);
        }

        private String oper;

        NodeType(String oper) {
            this.oper = oper;
        }

        public String getOper() {
            return oper;
        }
    }

    abstract public NodeType getNodeType();

    public String getUrl() {
        if (getNodeType() == NodeType.PEER || getNodeType() == NodeType.ORDERER) {
            StringBuilder url = new StringBuilder();
            if (tlsEnable) {
                url.append("grpcs://");
            } else {
                url.append("grpc://");
            }
            return url.append(ip).append(":").append(port).toString();
        } else {
            return null;
        }
    }

    public void setHostsName(List<String> hostsName) {
        this.hostsName = hostsName;
    }

    public void setHostsName(String hostName) {
        if (null != hostName && !hostName.isEmpty()) {
            ArrayList<String> hostsName = new ArrayList<>();
            hostsName.add(hostName);
            this.hostsName = hostsName;
        }
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        if (null != getMsp() && getTlsEnable()) {
            String ca = getMsp().getTlsCaCerts();
            if (StringUtils.isNotEmpty(ca)) {
                properties.put("pemBytes", ca.getBytes());
                if (getDoubleVerity()) {
                    //如果peer节点没有开启TLS双端认证这个配置就不能给，不然TLS握手失败
                    properties.put("clientCertBytes", getMsp().getTlsCerts().getBytes());
                    properties.put("clientKeyBytes", getMsp().getTlsKey().getBytes());
                }

                properties.setProperty("sslProvider", "openSSL");
                properties.setProperty("negotiationType", "TLS");

                //信任服务端证书
                properties.setProperty("trustServerCertificate", "true");
            }
        }
        return properties;
    }
}
