package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.common.bean.node.CouchdbInfo;
import org.hyperledger.justitia.common.face.dao.mapper.CouchdbMapper;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class CouchdbDao {
    private final CouchdbMapper couchdbMapper;

    public CouchdbDao(CouchdbMapper couchdbMapper) {
        this.couchdbMapper = couchdbMapper;
    }

    public int insertCouchdb(CouchdbInfo couchdbInfo) {
        try{
            return couchdbMapper.insertSelective(couchdbInfo);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "couchdb", couchdbInfo.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    public int updateCouchdb(CouchdbInfo couchdbInfo) {
        return couchdbMapper.updateByPrimaryKeySelective(couchdbInfo);
    }

    public int deleteCouchdbById(String id) {
        return couchdbMapper.deleteByPrimaryKey(id);
    }
}
