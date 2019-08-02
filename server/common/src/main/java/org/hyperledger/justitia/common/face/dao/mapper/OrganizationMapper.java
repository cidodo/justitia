package org.hyperledger.justitia.common.face.dao.mapper;

import org.hyperledger.justitia.common.bean.identity.Organization;

public interface OrganizationMapper {
    int deleteByPrimaryKey(String id);

    int insert(Organization record);

    int insertSelective(Organization record);

    Organization selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Organization record);

    int updateByPrimaryKey(Organization record);





    Organization getOrganizationBase();
    Organization getOrganizationWithMsp();
    Organization getOrganizationWithCrypto();
    int deleteOrganization();
}