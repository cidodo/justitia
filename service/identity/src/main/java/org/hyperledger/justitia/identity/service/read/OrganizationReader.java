package org.hyperledger.justitia.identity.service.read;

import org.hyperledger.justitia.identity.service.beans.OrganizationInfo;
import org.springframework.stereotype.Service;


public interface OrganizationReader {
    String getMspId();
    String getName();
    OrganizationInfo getOrganizationInfo();
    OrganizationInfo getOrganizationInfoWithCryptoMsp(String organizationId);
    OrganizationInfo getOrganizationInfoWithCrypto();
}
