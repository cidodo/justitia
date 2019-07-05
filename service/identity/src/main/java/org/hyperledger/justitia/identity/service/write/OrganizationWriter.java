package org.hyperledger.justitia.identity.service.write;

import org.hyperledger.justitia.identity.service.beans.OrganizationInfo;

public interface OrganizationWriter {
    void setOrganization(OrganizationInfo organizationInfo);
    void updateOrganizationInfo(OrganizationInfo organizationInfo);
    void deleteOrganization();
}
