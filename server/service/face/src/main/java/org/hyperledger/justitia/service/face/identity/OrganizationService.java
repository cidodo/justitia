package org.hyperledger.justitia.service.face.identity;

import org.hyperledger.justitia.service.face.identity.bean.OrganizationInfo;

public interface OrganizationService {
    String getMspId();
    String getName();
    OrganizationInfo getOrganizationInfo();
    void setOrganization(OrganizationInfo organizationInfo);
    void updateOrganizationInfo(OrganizationInfo organizationInfo);
    void deleteOrganization();
}
