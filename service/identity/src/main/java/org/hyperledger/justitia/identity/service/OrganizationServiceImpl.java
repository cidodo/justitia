package org.hyperledger.justitia.identity.service;

import org.hyperledger.justitia.service.face.identity.OrganizationService;
import org.hyperledger.justitia.service.face.identity.bean.OrganizationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final IdentityConfig identityConfig;

    @Autowired
    public OrganizationServiceImpl(IdentityConfig identityConfig) {
        this.identityConfig = identityConfig;
    }

    @Override
    public String getMspId() {
        return identityConfig.getMspId();
    }

    @Override
    public String getName() {
        OrganizationInfo organization = identityConfig.getOrganization();
        if (null == organization) {
            return null;
        }
        return organization.getName();
    }

    @Override
    public OrganizationInfo getOrganizationInfo() {
        return identityConfig.getOrganization();
    }

    @Override
    public void setOrganization(OrganizationInfo organizationInfo) {
        identityConfig.setOrganization(organizationInfo);
    }

    @Override
    public void updateOrganizationInfo(OrganizationInfo organizationInfo) {
        identityConfig.updateOrganization(organizationInfo);
    }

    @Override
    public void deleteOrganization() {
        identityConfig.deleteOrganization();
    }
}
