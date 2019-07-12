package org.hyperledger.justitia.common.face.modules.channel;


import java.util.List;

public interface ConsortiumManageService {
    List<ConsortiumInfo> getConsortiums(String ordererName);
    void addConsortiumMember(String ordererName, String consortiumName, String orgName, String orgConfig);
    void deleteConsortiumMember(String ordererName, String consortiumName, String orgName);
}
