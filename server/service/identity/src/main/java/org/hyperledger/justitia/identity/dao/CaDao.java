package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.common.bean.identity.crypto.Ca;
import org.hyperledger.justitia.common.face.dao.mapper.CaMapper;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import static org.hyperledger.justitia.common.utils.ParameterCheckUtils.*;

@Component
public class CaDao {
    private final CaMapper caMapper;

    @Autowired
    public CaDao(CaMapper caMapper) {
        this.caMapper = caMapper;
    }

    public int insertSignRootCa(Ca ca) {
        notNull(ca, "Ca is null.");
        ca.setCaType("sign");
        return insertCa(ca);
    }

    public int insertTlsRootCa(Ca ca) {
        notNull(ca, "Ca is null.");
        ca.setCaType("tls");
        return insertCa(ca);
    }

    private int insertCa(Ca ca) {
        try {
            return caMapper.insertSelective(ca);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "ca", ca.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    public int updateSignRootCa(Ca ca) {
        notNull(ca, "Ca is null.");
        return caMapper.updateByPrimaryKeySelective(ca);
    }

    public int updateTlsRootCa(Ca ca) {
        notNull(ca, "Ca is null.");
        return caMapper.updateByPrimaryKeySelective(ca);
    }

    public int deleteCa(String id) {
        return caMapper.deleteByPrimaryKey(id);
    }
}
