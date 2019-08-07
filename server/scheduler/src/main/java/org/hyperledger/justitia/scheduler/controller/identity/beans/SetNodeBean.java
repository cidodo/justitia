package org.hyperledger.justitia.scheduler.controller.identity.beans;

import lombok.Data;

import java.util.List;

@Data
public class SetNodeBean extends CryptoBean{
    private String name;
    private String ip;
    private Integer port;
    private String containerId;

    //tlsInfo
    private Boolean tlsEnable;
    private Boolean doubleVerify;
    private List<String> hostsName;
}
