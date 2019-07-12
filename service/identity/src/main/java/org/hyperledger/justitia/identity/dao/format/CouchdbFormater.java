package org.hyperledger.justitia.identity.dao.format;

import org.hyperledger.justitia.dao.bean.Couchdb;
import org.hyperledger.justitia.common.face.modules.identity.beans.CouchdbInfo;

public class CouchdbFormater {
    public static CouchdbInfo couchdb2CouchdbInfo(Couchdb couchdb) {
        if (null == couchdb) {
            return null;
        }

        CouchdbInfo couchdbInfo = new CouchdbInfo();
        couchdbInfo.setId(couchdb.getId());
        couchdbInfo.setIp(couchdb.getIp());
        couchdbInfo.setPort(couchdb.getPort());
        couchdbInfo.setTlsEnable(couchdb.getTlsEnable());
        couchdbInfo.setDoubleVerity(couchdb.getDoubleVerify());
        if (null != couchdb.getContainer()) {
            couchdbInfo.setSslTarget(couchdb.getContainer().getContainerName());
        }
        couchdbInfo.setCrypto(MspFormater.msp2NodeCrypto(couchdb.getMsp()));
        return couchdbInfo;
    }

    public static Couchdb couchdbInfo2Couchdb(String id,CouchdbInfo couchdbInfo) {
        if (null == couchdbInfo) {
            return null;
        }
        Couchdb couchdb = new Couchdb();
        couchdb.setId(id);
        if (null != couchdbInfo.getIp()) {
            couchdb.setIp(couchdbInfo.getIp());
        }
        if (null != couchdbInfo.getPort() && 0 != couchdbInfo.getPort()) {
            couchdb.setPort(couchdbInfo.getPort());
        }
        if (null != couchdbInfo.getContainerId()) {
            couchdb.setContainerId(couchdbInfo.getContainerId());
        }
        //Fixme 默认情况下couchdb不使用tls
        couchdb.setTlsEnable(false);
        couchdb.setDoubleVerify(false);
        couchdb.setMspId(null);

        return couchdb;
    }
}
