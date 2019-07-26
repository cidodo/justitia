package org.hyperledger.justitia.dao.bean;

import lombok.Data;

@Data
public class Ca{
    private String id;
    private String name;
    private Boolean root;
    private String parent;
    private String caType;
    private String caServer;
    private String cert;
    private String key;
}