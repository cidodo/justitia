package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.dao.bean.Couchdb;
import org.hyperledger.justitia.dao.mapper.CouchdbMapper;
import org.hyperledger.justitia.identity.dao.format.CouchdbFormater;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.identity.service.beans.CouchdbInfo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class CouchdbDao {
    private final CouchdbMapper couchdbMapper;

    public CouchdbDao(CouchdbMapper couchdbMapper) {
        this.couchdbMapper = couchdbMapper;
    }

    public int insertCouchdb(String id, CouchdbInfo couchdbInfo) {
        Couchdb couchdb = CouchdbFormater.couchdbInfo2Couchdb(id, couchdbInfo);
        try{
            return couchdbMapper.insertSelective(couchdb);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "couchdb", id);
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    public int updateCouchdb(String id, CouchdbInfo couchdbInfo) {
        Couchdb couchdb = CouchdbFormater.couchdbInfo2Couchdb(id, couchdbInfo);
        return couchdbMapper.updateByPrimaryKey(couchdb);
    }

    public int deleteCouchdbById(String id) {
        return couchdbMapper.deleteByPrimaryKey(id);
    }
}
