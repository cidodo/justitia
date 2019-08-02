package org.hyperledger.justitia.common.face.service.identity;


import org.hyperledger.justitia.common.bean.identity.Organization;

public interface OrganizationService {
    Organization getOrganization();
    void setOrganization(Organization organizationInfo);
    void updateOrganizationInfo(Organization organizationInfo);
    void deleteOrganization();
}
