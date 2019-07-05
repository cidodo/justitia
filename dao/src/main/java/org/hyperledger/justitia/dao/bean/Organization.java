package org.hyperledger.justitia.dao.bean;

import lombok.Data;

import java.util.List;

@Data
public class Organization {
    private String id;
    private String name;
    private String type;
    private String mspId;
    private String mspTabId;
    private String caId;
    private Boolean tlsEnable;
    private String tlsCaId;

    private Msp msp;
    private Ca ca;
    private Ca tlsCa;
    private List<FabricUser> adminUsers;
}