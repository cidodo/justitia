package org.hyperledger.justitia.identity.service;

import org.hyperledger.justitia.common.RequestContext;
import org.hyperledger.justitia.common.bean.identity.Organization;
import org.hyperledger.justitia.common.face.service.identity.OrganizationService;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.hyperledger.justitia.identity.exception.IdentityException.NO_ORGANIZATION;
import static org.hyperledger.justitia.identity.exception.IdentityException.ORGANIZATION_DOES_NOT_EXITS;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final IdentityConfig identityConfig;

    @Autowired
    public OrganizationServiceImpl(IdentityConfig identityConfig) {
        this.identityConfig = identityConfig;
    }

    private String getCurrentOrganizationId(boolean check) {
        final String organizationId = RequestContext.getOrganizationId();
        if (null == organizationId || organizationId.isEmpty()) {
            if (check) {
                throw new IllegalArgumentException("Organization id is empty.");
            } else {
                return null;
            }
        }
        return organizationId;
    }

    @Override
    public Organization getOrganization() {
        final String organizationId = getCurrentOrganizationId(false);
        final Organization organization;
        if (null == organizationId) {
            organization = identityConfig.getOrganization();
            if (null == organization) {
                throw new IdentityException(NO_ORGANIZATION);
            }
        } else {
            organization = identityConfig.getOrganization(organizationId);
            if (null == organization) {
                throw new IdentityException(ORGANIZATION_DOES_NOT_EXITS, organizationId);
            }
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
        identityConfig.deleteOrganization(getCurrentOrganizationId(true));
    }
}
