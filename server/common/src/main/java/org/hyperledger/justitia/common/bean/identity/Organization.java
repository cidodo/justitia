package org.hyperledger.justitia.common.bean.identity;

import lombok.Data;
import org.hyperledger.justitia.common.bean.identity.crypto.Ca;
import org.hyperledger.justitia.common.bean.identity.crypto.Crypto;
import org.hyperledger.justitia.common.bean.identity.crypto.Msp;
import org.hyperledger.justitia.common.bean.node.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Organization extends Crypto{
    private String id;
    private String name;
    private String mspId;
    private String type;
    private Boolean tlsEnable;

    private List<FabricUser> users;
    private List<Node> nodes;

    @Override
    protected String generateMspId() {
        return id + "-org-msp";
    }

    @Override
    protected String generateCaId() {
        return id + "-org-ca";
    }

    @Override
    protected String generateTlsCaId() {
        return id + "-org-tls-ca";
    }


    public enum OrganizationType {
        PEER_ORGANIZATION("peerOrganization"),
        ORDERER_ORGANIZATION("ordererOrganization");

        private final static Map<String, OrganizationType> ENUM_MAP = new HashMap<>();

        static {
            for (OrganizationType v : values()) {
                ENUM_MAP.put(v.getOper(), v);
            }
        }

        public static OrganizationType fromString(String oper) {
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

    public OrganizationType getType() {
        return OrganizationType.fromString(type);
    }

    public List<FabricUser> getAdminUser() {
        ArrayList<FabricUser> adminUsers = new ArrayList<>();
        if (null != users) {
            for (FabricUser user : users) {
                if (user.getAdmin()) {
                    adminUsers.add(user);
                }
            }
        }
        return adminUsers;
    }
}