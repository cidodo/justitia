package org.hyperledger.justitia.dao.bean;

import lombok.Data;

@Data
public class FabricUser {
    private String id;
    private String organizationId;
    private Boolean admin;
    private String mspId;
    private String tag;

    private Msp msp;
}