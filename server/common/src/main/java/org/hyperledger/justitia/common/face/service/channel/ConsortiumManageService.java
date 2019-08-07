package org.hyperledger.justitia.common.face.service.channel;


import org.hyperledger.justitia.common.bean.channel.ChannelMember;
import org.hyperledger.justitia.common.bean.channel.ConsortiumInfo;

import java.util.List;

public interface ConsortiumManageService {
    List<ConsortiumInfo> getConsortiums(String ordererName);
    void addConsortiumMember(String ordererName, String consortiumName, ChannelMember member);
    void updateConsortiumMember(String ordererId, String consortiumName, ChannelMember member);
    void deleteConsortiumMember(String ordererName, String consortiumName, String orgName);
}
