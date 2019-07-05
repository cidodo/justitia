package org.hyperledger.justitia.dao.bean;

import lombok.Data;


@Data
public class Couchdb {
    private String id;
    private String ip;
    private Integer port;
    private String containerId;
    private String mspId;
    private Boolean tlsEnable;
    private Boolean doubleVerify;

    private Container container;
    private Msp msp;
}