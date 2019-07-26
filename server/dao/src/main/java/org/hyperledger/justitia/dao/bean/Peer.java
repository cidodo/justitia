package org.hyperledger.justitia.dao.bean;

import lombok.Data;


@Data
public class Peer {
    private String id;
    private String ip;
    private Integer port;
    private String containerId;
    private String mspId;
    private Boolean tlsEnable;
    private Boolean doubleVerify;
    private Boolean couchdbEnable;
    private String couchdbId;



    private Msp msp;
    private Container container;
    private Couchdb couchdb;
}