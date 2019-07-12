package org.hyperledger.justitia.common.face.modules.identity.read;

import org.hyperledger.justitia.common.face.modules.identity.beans.OrganizationInfo;


public interface OrganizationReader {
    String getMspId();
    String getName();
    OrganizationInfo getOrganizationInfo();
    OrganizationInfo getOrganizationInfoWithCryptoMsp(String organizationId);
    OrganizationInfo getOrganizationInfoWithCrypto();
}
