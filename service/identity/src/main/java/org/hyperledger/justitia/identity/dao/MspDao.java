package org.hyperledger.justitia.identity.dao;

import org.hyperledger.justitia.dao.bean.Msp;
import org.hyperledger.justitia.dao.mapper.MspMapper;
import org.hyperledger.justitia.identity.dao.format.MspFormater;
import org.hyperledger.justitia.identity.exception.IdentityDuplicateKeyException;
import org.hyperledger.justitia.identity.service.beans.crypto.MspInfo;
import org.hyperledger.justitia.identity.service.beans.crypto.TlsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
public class MspDao {
    private final MspMapper mspMapper;

    @Autowired
    public MspDao(MspMapper mspMapper) {
        this.mspMapper = mspMapper;
    }

    public int insertOrganizationMsp(String id, MspInfo mspInfo) {
        if (null == id || null == mspInfo) {
            return 0;
        }
        Msp msp = MspFormater.mspInfo2Msp(id, mspInfo);
        return insertMsp(msp);
    }

    public int updateOrganizationMsp(String id, MspInfo mspInfo) {
        if (null == id || null == mspInfo) {
            return 0;
        }
        Msp msp = MspFormater.mspInfo2Msp(id, mspInfo);
        return updateMsp(msp);
    }

    public int insertPeerMsp(String id, MspInfo mspInfo, TlsInfo tlsInfo) {
        return insertNodeMsp(id, mspInfo, tlsInfo);
    }

    public int updatePeerMsp(String id, MspInfo mspInfo, TlsInfo tlsInfo) {
        return updateNodeMsp(id, mspInfo, tlsInfo);
    }

    public int insertOrdererMsp(String id, MspInfo mspInfo, TlsInfo tlsInfo) {
        return insertNodeMsp(id, mspInfo, tlsInfo);
    }

    public int updateOrdererMsp(String id, MspInfo mspInfo, TlsInfo tlsInfo) {
        return updateNodeMsp(id, mspInfo, tlsInfo);
    }

    private int insertNodeMsp(String id, MspInfo mspInfo, TlsInfo tlsInfo) {
        if (null == id) {
            return 0;
        }
        Msp msp = MspFormater.mspInfo2Msp(id, mspInfo, tlsInfo);
        return insertMsp(msp);
    }

    private int updateNodeMsp(String id, MspInfo mspInfo, TlsInfo tlsInfo) {
        if (null == id || (null == mspInfo && null == tlsInfo)) {
            return 0;
        }
        Msp msp = MspFormater.mspInfo2Msp(id, mspInfo, tlsInfo);
        return updateMsp(msp);
    }

    public int insertUserMsp(String id, MspInfo mspInfo, TlsInfo tlsInfo) {
        if (null == id) {
            return 0;
        }
        Msp msp = MspFormater.mspInfo2Msp(id, mspInfo, tlsInfo);
        return insertMsp(msp);
    }

    public int updateUserMsp(String id, MspInfo mspInfo, TlsInfo tlsInfo) {
        if (null == id) {
            return 0;
        }
        Msp msp = MspFormater.mspInfo2Msp(id, mspInfo, tlsInfo);
        return updateMsp(msp);
    }


    private int insertMsp(Msp msp) {
        try{
             return mspMapper.insertSelective(msp);
        } catch (DuplicateKeyException e) {
            String msg = String.format("The %s with key %s is already present.", "msp", msp.getId());
            throw new IdentityDuplicateKeyException(msg);
        }
    }

    private int updateMsp(Msp msp) {
        return mspMapper.updateByPrimaryKeySelective(msp);
    }

    public int deleteMspById(String id) {
        return mspMapper.deleteByPrimaryKey(id);
    }

}
