package org.hyperledger.justitia.identity.service.read;

import org.hyperledger.justitia.identity.dao.OrganizationDao;
import org.hyperledger.justitia.common.face.modules.identity.beans.OrganizationInfo;
import org.hyperledger.justitia.common.face.modules.identity.read.OrganizationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationReaderImpl implements OrganizationReader {
    private final OrganizationDao organizationDao;

    @Autowired
    public OrganizationReaderImpl(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    @Override
    public String getMspId() {
        OrganizationInfo organizationBaseInfo = organizationDao.getOrganizationBaseInfo();
        if (null == organizationBaseInfo) {
            return null;
        }
        return organizationBaseInfo.getMspId();
    }

    @Override
    public String getName() {
        OrganizationInfo organizationBaseInfo = organizationDao.getOrganizationBaseInfo();
        if (null == organizationBaseInfo) {
            return null;
        }
        return organizationBaseInfo.getName();
    }

    @Override
    public OrganizationInfo getOrganizationInfo() {
        return organizationDao.getOrganizationBaseInfo();
    }

    @Override
    public OrganizationInfo getOrganizationInfoWithCryptoMsp(String organizationId) {
        return organizationDao.getOrganizationInfoWithCryptoMsp();
    }

    @Override
    public OrganizationInfo getOrganizationInfoWithCrypto() {
        return organizationDao.getOrganizationInfoWithCrypto();
    }
}
