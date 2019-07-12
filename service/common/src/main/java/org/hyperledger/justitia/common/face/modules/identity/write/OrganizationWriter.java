package org.hyperledger.justitia.common.face.modules.identity.write;

import org.hyperledger.justitia.common.face.modules.identity.beans.OrganizationInfo;

public interface OrganizationWriter {
    void setOrganization(OrganizationInfo organizationInfo);
    void updateOrganizationInfo(OrganizationInfo organizationInfo);
    void deleteOrganization();
}
