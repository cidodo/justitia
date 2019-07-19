package org.hyperledger.justitia.service.face.identity.bean;

import lombok.Data;
import org.hyperledger.justitia.common.utils.StringUtils;
import org.hyperledger.justitia.service.face.identity.bean.crypto.NodeCrypto;
import org.hyperledger.justitia.service.face.identity.bean.crypto.TlsInfo;

import java.util.*;

@Data
public class NodeInfo {
    //base info
    private String id;
    private String ip;
    private Integer port;
    private String containerId;
    //TLS
    private Boolean tlsEnable;
    private Boolean doubleVerity;
    private List<String> sslTarget;
    //crypto
    private NodeCrypto crypto;
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

    public String getUrl() {
        if (this.getClass() == PeerInfo.class || this.getClass() == OrdererInfo.class) {
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

    public void setSslTarget(List<String> sslTarget) {
        this.sslTarget = sslTarget;
    }

    public void setSslTarget(String sslTarget) {
        if (null != sslTarget && !sslTarget.isEmpty()) {
            ArrayList<String> sslTargets = new ArrayList<>();
            sslTargets.add(sslTarget);
            this.sslTarget = sslTargets;
        }
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        if (getTlsEnable()) {
            TlsInfo tls = getCrypto().getTlsInfo();
            String ca = tls.getCa();
            if (StringUtils.isNotEmpty(ca)) {
                properties.put("pemBytes", ca.getBytes());
                if (getDoubleVerity()) {
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
        return properties;
    }
}
