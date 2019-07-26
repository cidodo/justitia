package org.hyperledger.justitia.dao.bean;

import lombok.Data;
//import org.apache.tomcat.util.buf.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Msp {
    private String id;
    private String adminUsersId;
    private String caCerts;
    private String intermediateCerts;
    private String crls;
    private String configFile;      //config.yaml
    private String keyStore;
    private String signCerts;
    private String tlsCaCerts;
    private String tlsIntermediateCerts;
    private String tlsCerts;
    private String tlsKey;

    public List<String> getAdminUsersIdAsList() {
        if (null == adminUsersId) {
            return null;
        }
        String[] usersId = adminUsersId.split(";");
        return new ArrayList<>(Arrays.asList(usersId));
    }

    public void setAdminUsersIdList(List<String> adminUsersId) {
//        this.adminUsersId = StringUtils.join(adminUsersId, ';');
    }

    public List<String> getIntermediateCertsAsList() {
        if (null == intermediateCerts) {
            return null;
        }
        String[] usersId = intermediateCerts.split(";");
        return new ArrayList<>(Arrays.asList(usersId));
    }

    public void setIntermediateCertsList(List<String> intermediateCerts) {
//        this.intermediateCerts = StringUtils.join(intermediateCerts, ';');
    }

    public List<String> getCrlsAsList() {
        if (null == crls) {
            return null;
        }
        String[] usersId = crls.split(";");
        return new ArrayList<>(Arrays.asList(usersId));
    }

    public void setCrlsList(List<String> crls) {
//        this.crls = StringUtils.join(crls, ';');
    }

    public List<String> getTlsIntermediateCertsAsList() {
        if (null == tlsIntermediateCerts) {
            return null;
        }
        String[] usersId = tlsIntermediateCerts.split(";");
        return new ArrayList<>(Arrays.asList(usersId));
    }

    public void setTlsIntermediateCertsList(List<String> tlsIntermediateCerts) {
//        this.tlsIntermediateCerts = StringUtils.join(tlsIntermediateCerts, ';');
    }
}
