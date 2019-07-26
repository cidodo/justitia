package org.hyperledger.justitia.scheduler.controller.chaincode.beans;

import lombok.Data;

import java.util.ArrayList;

@Data
public class InvokeChaincode {
    private String channelName;
    private String chaincodeName;
    private String function;
    private ArrayList<String> args;
    private ArrayList<String> peersName;
}
