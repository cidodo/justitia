package org.hyperledger.justitia.dao.bean;

import lombok.Data;

@Data
public class Orderer {
    private String id;
    private String ip;
    private Integer port;
    private String containerId;
    private String mspId;
    private Boolean tlsEnable;
    private Boolean doubleVerify;
    private String systemChainId;


    private Msp msp;
    private Container container;
}