package org.hyperledger.justitia.identity.dao.format;

import org.hyperledger.justitia.dao.bean.Peer;
import org.hyperledger.justitia.common.face.modules.identity.beans.PeerInfo;

import java.util.ArrayList;
import java.util.List;

public class PeerFormater {
    public static PeerInfo peer2PeerInfo(Peer peer) {
        if (null == peer) {
            return null;
        }

        PeerInfo peerInfo = new PeerInfo();
        peerInfo.setId(peer.getId());
        peerInfo.setIp(peer.getIp());
        peerInfo.setPort(peer.getPort());
        peerInfo.setContainerId(peer.getContainerId());
        peerInfo.setTlsEnable(peer.getTlsEnable());
        peerInfo.setDoubleVerity(peer.getDoubleVerify());
        if (null != peer.getContainer()) {
            peerInfo.setSslTarget(peer.getContainer().getContainerName());
        }
        peerInfo.setCrypto(MspFormater.msp2NodeCrypto(peer.getMsp()));
        peerInfo.setCouchdbEnable(peer.getCouchdbEnable());
        peerInfo.setCouchdb(CouchdbFormater.couchdb2CouchdbInfo(peer.getCouchdb()));
        return peerInfo;
    }


    public static List<PeerInfo> peers2PeersInfo(List<Peer> peers) {
        if (null == peers || peers.isEmpty()) {
            return null;
        }

        List<PeerInfo> peersInfo = new ArrayList<>();
        for (Peer peer : peers) {
            PeerInfo peerInfo = PeerFormater.peer2PeerInfo(peer);
            peersInfo.add(peerInfo);
        }

        return peersInfo;
    }

    public static Peer peerInfo2Peer(PeerInfo peerInfo, String mspId) {
        return peerInfo2Peer(peerInfo, mspId, null);
    }

    public static Peer peerInfo2Peer(PeerInfo peerInfo, String mspId, String couchdbId) {
        if (null == peerInfo) {
            return null;
        }

        Peer peer = new Peer();

        if (null != peerInfo.getId()) {
            peer.setId(peerInfo.getId());
        }

        if (null != peerInfo.getIp()) {
            peer.setIp(peerInfo.getIp());
        }

        if (null != peerInfo.getPort()) {
            peer.setPort(peerInfo.getPort());
        }

        if (null != peerInfo.getContainerId()) {
            peer.setContainerId(peerInfo.getContainerId());
        }

        if (null != mspId && !mspId.isEmpty()) {
            peer.setMspId(mspId);
        }

        if (null != peerInfo.getTlsEnable()) {
            peer.setTlsEnable(peerInfo.getTlsEnable());
        }

        if (null != peerInfo.getDoubleVerity()) {
            peer.setDoubleVerify(peerInfo.getDoubleVerity());
        }

        if (null != peerInfo.getCouchdbEnable()) {
            peer.setCouchdbEnable(peerInfo.getCouchdbEnable());
        }

        if (null != couchdbId && !couchdbId.isEmpty()) {
            peer.setCouchdbId(couchdbId);
        }
        return peer;
    }
}
