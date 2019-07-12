package org.hyperledger.justitia.identity.service.write;

import org.hyperledger.justitia.identity.dao.OrganizationDao;
import org.hyperledger.justitia.identity.exception.IdentityException;
import org.hyperledger.justitia.common.face.modules.identity.beans.OrganizationInfo;
import org.hyperledger.justitia.common.face.modules.identity.write.OrganizationWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationWriterImpl implements OrganizationWriter {
    private final OrganizationDao organizationDao;

    @Autowired
    public OrganizationWriterImpl(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    @Override
    public void setOrganization(OrganizationInfo organizationInfo) {
        OrganizationInfo organizationBaseInfo = organizationDao.getOrganizationBaseInfo();
        if (null != organizationBaseInfo) {
            String msg = String.format("只支持配置单个组织, 现在已经存在一个名为%s的组织。", organizationBaseInfo.getName());
//            String msg = String.format("Only a single organization is supported, and organization %s already exists.", organizationBaseInfo.getName());
            throw new IdentityException(msg);
        }
        int rowCount = organizationDao.insertOrganization(organizationInfo);
        if (1 != rowCount) {
            throw new IdentityException("设置组织配置失败。");
//            throw new IdentityException("Set organization configuration failed.");
        }
    }

    @Override
    public void updateOrganizationInfo(OrganizationInfo organizationInfo) {
        if (null == organizationInfo.getType()) {
            throw new IdentityException("组织类型为空。");
//            throw new IdentityException("Organization type is null.");
        }
        int rowCount = organizationDao.updateOrgainzation(organizationInfo);
        if (1 != rowCount) {
            throw new IdentityException("更新组织配置失败。");
//            throw new IdentityException("Update organization configuration failed.");
        }
    }

    @Override
    public void deleteOrganization() {
        OrganizationInfo organizationBaseInfo = organizationDao.getOrganizationBaseInfo();
        if (null == organizationBaseInfo) {
            throw new IdentityException("No organization exists and cannot be deleted.");
//            throw new IdentityException("No organization exists and cannot be deleted.");
        }
        int rowCount = organizationDao.deleteOrganization(organizationBaseInfo.getId());
        if (1 != rowCount) {
            throw new IdentityException("Delete organization configuration failed.");
        }
    }
}
