package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.common.bean.identity.Organization;
import org.hyperledger.justitia.common.bean.identity.crypto.Ca;
import org.hyperledger.justitia.common.bean.identity.crypto.Msp;
import org.hyperledger.justitia.common.face.dao.mapper.OrganizationMapper;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.notNull;
import static org.hyperledger.justitia.identity.exception.IdentityException.*;

@Component
public class OrganizationDao {
    private final OrganizationMapper organizationMapper;
    private final MspDao mspDao;
    private final CaDao caDao;

    @Autowired
    public OrganizationDao(OrganizationMapper organizationMapper, MspDao mspDao, CaDao caDao) {
        this.organizationMapper = organizationMapper;
        this.mspDao = mspDao;
        this.caDao = caDao;
    }

    public List<Organization> selectOrganization() {
        return null;
    }

    public Organization getOrganization(String orgId) {
        return organizationMapper.selectByPrimaryKey(orgId);
    }

    @Transactional
    public int insertOrganization(Organization organization) {
        notNull(organization, "Organization is null.");

        //mspInfo
        Msp msp = organization.getMsp();
        if (null != msp) {
            mspDao.insertMsp(msp);
        }

        //Ca
        Ca ca = organization.getCa();
        if (null == ca) {
            throw new IdentityException(INCOMPLETE_CA);
        }else {
            caDao.insertSignRootCa(ca);
        }

        //tlsca
        Ca tlsCa = organization.getTlsCa();
        if (organization.getTlsEnable() && null == tlsCa) {
            throw new IdentityException(INCOMPLETE_TLSCA);
        } else {
            caDao.insertTlsRootCa(organization.getTlsCa());
        }

        try {
            return organizationMapper.insertSelective(organization);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "organization", organization.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    @Transactional
    public int updateOrganization(Organization organization) {
        notNull(organization, "Organization is null.");

        //mspInfo
        Msp msp = organization.getMsp();
        if (null != msp) {
            mspDao.updateMsp(msp);
        }

        //tlsCa
        Ca ca = organization.getCa();
        if (null != ca) {
            caDao.updateSignRootCa(ca);
        }

        //tlsca
        Ca tlsca = organization.getTlsCa();
        if (null != tlsca) {
            caDao.updateTlsRootCa(tlsca);
        }

        return organizationMapper.updateByPrimaryKeySelective(organization);
    }

    @Transactional
    public int deleteOrganization(String orgId) {
        Organization organization = getOrganization(orgId);
        if (null == organization) {
            throw new IdentityException(ORGANIZATION_DOES_NOT_EXITS, orgId);
        }

        Msp msp = organization.getMsp();
        if (null != msp) {
            mspDao.deleteMspById(msp.getId());
        }
        Ca ca = organization.getCa();
        if (null != ca) {
            caDao.deleteCa(ca.getId());
        }
        Ca tlsCa = organization.getTlsCa();
        if (null != tlsCa) {
            caDao.deleteCa(tlsCa.getId());
        }
        return organizationMapper.deleteByPrimaryKey(orgId);
    }
}
