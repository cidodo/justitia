package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.dao.bean.Organization;
import org.hyperledger.justitia.dao.mapper.OrganizationMapper;
import org.hyperledger.justitia.identity.dao.format.OrganizationFormater;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.identity.service.beans.OrganizationInfo;
import org.hyperledger.justitia.identity.service.beans.crypto.CaInfo;
import org.hyperledger.justitia.identity.service.beans.crypto.MspInfo;
import org.hyperledger.justitia.identity.service.beans.crypto.OrganizationCrypto;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrganizationDao {
    private final OrganizationMapper organizationMapper;
    private final MspDao mspDao;
    private final CaDao caDao;

    public OrganizationDao(OrganizationMapper organizationMapper, MspDao mspDao, CaDao caDao) {
        this.organizationMapper = organizationMapper;
        this.mspDao = mspDao;
        this.caDao = caDao;
    }

    public OrganizationInfo getOrganizationBaseInfo() {
        Organization organizationBaseInfo = organizationMapper.getOrganizationBase();
        return OrganizationFormater.organization2OrganizationInfo(organizationBaseInfo);
    }

    public OrganizationInfo getOrganizationInfoWithCryptoMsp() {
        Organization organizationInfo = organizationMapper.getOrganizationWithMsp();
        return OrganizationFormater.organization2OrganizationInfo(organizationInfo);
    }

    public OrganizationInfo getOrganizationInfoWithCrypto() {
        Organization organizationInfo = organizationMapper.getOrganizationWithCrypto();
        return OrganizationFormater.organization2OrganizationInfo(organizationInfo);
    }

    @Transactional
    public int insertOrganization(OrganizationInfo organizationInfo) {
        if (null == organizationInfo) {
            return 0;
        }
        Organization organization = new Organization();
        organization.setId(organizationInfo.getId());
        organization.setName(organizationInfo.getName());
        organization.setType(organizationInfo.getType().getOper());
        organization.setMspId(organizationInfo.getMspId());
        organization.setTlsEnable(organizationInfo.getTlsEnable());

        OrganizationCrypto crypto = organizationInfo.getCrypto();
        if (null != crypto) {
            //mspInfo
            String mspTabId = generateMspId(organizationInfo.getId());
            if(1 == mspDao.insertOrganizationMsp(mspTabId, crypto.getMsp())) {
                organization.setMspTabId(mspTabId);
            }

            //tlsCa
            String caId = generateCaId(organizationInfo.getId());
            if(1 == caDao.insertSignRootCa(caId, crypto.getCa())) {
                organization.setCaId(caId);
            }

            //tlsca
            String tlsCaId = generateTlsCaId(organizationInfo.getId());
            if(1 == caDao.insertTlsRootCa(tlsCaId, crypto.getTlsca())){
                organization.setTlsCaId(tlsCaId);
            }
        }

        try {
            return organizationMapper.insertSelective(organization);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "organization", organization.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    @Transactional
    public int updateOrgainzation(OrganizationInfo organizationInfo) {
        if (null == organizationInfo) {
            return 0;
        }

        OrganizationCrypto crypto = organizationInfo.getCrypto();
        if (null != crypto) {
            //mspInfo
            MspInfo msp = crypto.getMsp();
            if (null != msp) {
                mspDao.updateOrganizationMsp(generateMspId(organizationInfo.getId()), msp);
            }

            //tlsCa
            CaInfo ca = crypto.getCa();
            if (null != ca) {
                caDao.updateSignRootCa(generateCaId(organizationInfo.getId()), ca);
            }

            //tlsca
            CaInfo tlsca = crypto.getTlsca();
            if (null != tlsca) {
                caDao.updateTlsRootCa(generateTlsCaId(organizationInfo.getId()), tlsca);
            }
        }

        //organization
        Organization organization = new Organization();
        organization.setId(organizationInfo.getId());
        organization.setName(organizationInfo.getName());
        organization.setType(organizationInfo.getType().getOper());
        organization.setMspId(organizationInfo.getMspId());
        organization.setTlsEnable(organizationInfo.getTlsEnable());
        return organizationMapper.updateByPrimaryKey(organization);
    }

    @Transactional
    public int deleteOrganization(String id) {
        mspDao.deleteMspById(generateMspId(id));
        caDao.deleteCa(generateCaId(id));
        caDao.deleteCa(generateTlsCaId(id));
        return organizationMapper.deleteByPrimaryKey(id);
    }

    private String generateMspId(String organizationId) {
        return organizationId + "-org-msp";
    }

    private String generateCaId(String organizationId) {
        return organizationId + "-ca";
    }

    private String generateTlsCaId(String organizaationId) {
        return organizaationId + "-tls-ca";
    }
}
