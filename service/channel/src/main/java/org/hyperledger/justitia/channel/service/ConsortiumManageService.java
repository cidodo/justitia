package org.hyperledger.justitia.channel.service;

import org.hyperledger.justitia.channel.service.beans.ConsortiumInfo;

import java.util.List;

public interface ConsortiumManageService {
    List<ConsortiumInfo> getConsortiums(String ordererName);
    void addConsortiumMember(String ordererName, String consortiumName, String orgName, String orgConfig);
    void deleteConsortiumMember(String ordererName, String consortiumName, String orgName);
}
