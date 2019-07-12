package org.hyperledger.justitia.common.face.modules.channel.beans;

import lombok.Data;

import java.util.Collection;

@Data
public class ConsortiumInfo {
    private String consortiumId;
    private Collection<String> members;

    public ConsortiumInfo(String consortiumId, Collection<String> members) {
        this.consortiumId = consortiumId;
        this.members = members;
    }

    public void addMember(String memberId) {
        members.add(memberId);
    }
}