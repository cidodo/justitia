package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.dao.bean.Ca;
import org.hyperledger.justitia.dao.mapper.CaMapper;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.common.face.modules.identity.beans.crypto.CaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
public class CaDao {
    private final CaMapper caMapper;

    @Autowired
    public CaDao(CaMapper caMapper) {
        this.caMapper = caMapper;
    }

    public int insertSignRootCa(String id, CaInfo caInfo) {
        if (null == id || null == caInfo) {
            return 0;
        }

        Ca ca = formatRootCa(id, caInfo);
        ca.setCaType("sign");
        return insertCa(ca);
    }

    public int insertTlsRootCa(String id, CaInfo caInfo) {
        if (null == id || null == caInfo) {
            return 0;
        }
        Ca ca = formatRootCa(id, caInfo);
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

    public int updateSignRootCa(String id, CaInfo caInfo) {
        if (null == id || null == caInfo) {
            return 0;
        }
        Ca ca = formatRootCa(id, caInfo);
        return caMapper.updateByPrimaryKeySelective(ca);
    }

    public int updateTlsRootCa(String id, CaInfo caInfo) {
        if (null == id || null == caInfo) {
            return 0;
        }
        Ca ca = formatRootCa(id, caInfo);
        return caMapper.updateByPrimaryKeySelective(ca);
    }

    private Ca formatRootCa(String id, CaInfo caInfo) {
        Ca ca = new Ca();
        ca.setId(id);
        ca.setName(id);
        ca.setRoot(true);
        ca.setParent(null);
        ca.setCaType(null);
        ca.setCaServer(null);
        ca.setCert(caInfo.getCert());
        ca.setKey(caInfo.getKey());
        return ca;
    }

    public int deleteCa(String id) {
        return caMapper.deleteByPrimaryKey(id);
    }
}
