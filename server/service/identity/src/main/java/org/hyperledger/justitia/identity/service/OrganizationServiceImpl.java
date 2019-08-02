package org.hyperledger.justitia.identity.service;

import org.hyperledger.justitia.common.RequestContext;
import org.hyperledger.justitia.common.bean.identity.Organization;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.hyperledger.justitia.common.face.service.identity.OrganizationService;
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
    public Organization getOrganization() {
        Organization organization;
        String organizationId = RequestContext.getOrganizationId();
        if (null == organizationId) {
            organization = identityConfig.getOrganization();
        } else {
            organization = identityConfig.getOrganization(organizationId);
        }
        if (null == organization) {
            throw new IdentityException();
        }
        return organization;
    }

    @Override
    public void setOrganization(Organization organization) {
        identityConfig.setOrganization(organization);
    }

    @Override
    public void updateOrganizationInfo(Organization organization) {
        identityConfig.updateOrganization(organization);
    }

    @Override
    public void deleteOrganization() {
        String organizationId = RequestContext.getOrganizationId();
        if (null == organizationId) {
            throw new IdentityException();
        }
        identityConfig.deleteOrganization(organizationId);
    }
}
