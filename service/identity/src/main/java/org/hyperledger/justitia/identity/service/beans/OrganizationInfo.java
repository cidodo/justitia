package org.hyperledger.justitia.identity.service.beans;

import lombok.Data;
import org.hyperledger.justitia.identity.service.beans.crypto.OrganizationCrypto;

import java.util.HashMap;
import java.util.Map;

@Data
public class OrganizationInfo {
    private String id;
    private String name;
    private OrganizationType type;
    private String mspId;
    private Boolean tlsEnable;
    private OrganizationCrypto crypto;

    public void setType(String type) {
        this.type = OrganizationType.fromString(type);
    }

    public enum OrganizationType {
        PEER_ORGANIZATION("peerOrganization"),
        ORDERER_ORGANIZATION("ordererOrganization");

        private final static Map<String, OrganizationInfo.OrganizationType> ENUM_MAP = new HashMap<>();

        static {
            for (OrganizationInfo.OrganizationType v : values()) {
                ENUM_MAP.put(v.getOper(), v);
            }
        }

        public static OrganizationInfo.OrganizationType fromString(String oper) {
            return ENUM_MAP.get(oper);
        }

        private String oper;

        OrganizationType(String oper) {
            this.oper = oper;
        }

        public String getOper() {
            return oper;
        }
    }
}


