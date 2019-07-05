package org.hyperledger.justitia.identity.service.beans;

import lombok.Data;
import org.hyperledger.justitia.identity.service.beans.crypto.NodeCrypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
