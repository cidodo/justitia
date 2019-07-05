package org.hyperledger.justitia.channel.service.beans;

import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class ConsortiumInfo {
    private String consortiumId;
    private Collection<String> members;

    public ConsortiumInfo(String consortiumId, Collection<String> members) {
        this.consortiumId = consortiumId;
        this.members = members;
    }

    public void addMembers(String memberId) {
        members.add(memberId);
    }
}
