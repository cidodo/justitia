/*
 *  Copyright 2017 DTCC, Fujitsu Australia Software Technology, IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.hyperledger.justitia.identity.service;

import org.hyperledger.justitia.identity.dao.FabricUserDao;
import org.hyperledger.justitia.identity.dao.OrdererDao;
import org.hyperledger.justitia.identity.dao.OrganizationDao;
import org.hyperledger.justitia.identity.dao.PeerDao;
import org.hyperledger.justitia.service.face.identity.bean.FabricUserInfo;
import org.hyperledger.justitia.service.face.identity.bean.OrdererInfo;
import org.hyperledger.justitia.service.face.identity.bean.OrganizationInfo;
import org.hyperledger.justitia.service.face.identity.bean.PeerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdentityConfig {
    private static OrganizationInfo organization = null;
    private static final Map<String, FabricUserInfo> adminUsers = new ConcurrentHashMap<>();
    private static final Map<String, FabricUserInfo> users = new ConcurrentHashMap<>();
    private static final Map<String, OrdererInfo> orderers = new ConcurrentHashMap<>();
    private static final Map<String, PeerInfo> peers = new ConcurrentHashMap<>();

    private final OrganizationDao organizationDao;
    private final FabricUserDao fabricUserDao;
    private final PeerDao peerDao;
    private final OrdererDao ordererDao;

    @Autowired
    public IdentityConfig(OrganizationDao organizationDao, FabricUserDao fabricUserDao, PeerDao peerDao, OrdererDao ordererDao) {
        this.organizationDao = organizationDao;
        this.fabricUserDao = fabricUserDao;
        this.peerDao = peerDao;
        this.ordererDao = ordererDao;
    }

    public String getMspId() {
        OrganizationInfo organization = getOrganization();
        if (null == organization) {
            return null;
        }
        return organization.getMspId();
    }

    public OrganizationInfo getOrganization() {
        if (null == organization) {
            organization = organizationDao.getOrganizationInfoWithCrypto();
        }
        return organization;
    }

    public void setOrganization(OrganizationInfo organizationInfo) {
        if (1 == organizationDao.insertOrganization(organizationInfo)) {
            IdentityConfig.organization = organizationInfo;
        }
    }

    public void updateOrganization(OrganizationInfo organizationInfo) {
        if (1 == organizationDao.updateOrgainzation(organizationInfo)) {
            IdentityConfig.organization = organizationInfo;
        }
    }

    public void deleteOrganization() {
        if (1 == organizationDao.deleteOrganization()) {
            IdentityConfig.organization = null;
        }
    }

    public List<FabricUserInfo> getUsers() {
        List<FabricUserInfo> fabricUsersInfo = fabricUserDao.selectUser();
        synchronized (this) {
            adminUsers.clear();
            users.clear();
            if (null != fabricUsersInfo) {
                String mspId = getMspId();
                for (FabricUserInfo fabricUserInfo : fabricUsersInfo) {
                    fabricUserInfo.setMspId(mspId);
                    if (fabricUserInfo.getAdmin()) {
                        adminUsers.put(fabricUserInfo.getId(), fabricUserInfo);
                    }
                    users.put(fabricUserInfo.getId(), fabricUserInfo);
                }
            }
        }
        return fabricUsersInfo;
    }

    public FabricUserInfo getAdminUser() {
        if (adminUsers.isEmpty()) {
            List<FabricUserInfo> adminUsersInfo = fabricUserDao.selectAdminsUser();
            synchronized (adminUsers) {
                adminUsers.clear();
                if (null != adminUsersInfo) {
                    String mspId = getMspId();
                    for (FabricUserInfo adminUserInfo : adminUsersInfo) {
                        adminUserInfo.setMspId(mspId);
                        adminUsers.put(adminUserInfo.getId(), adminUserInfo);
                    }
                }
            }
        }
        if (adminUsers.isEmpty()) {
            return null;
        } else {
            return adminUsers.entrySet().iterator().next().getValue();
        }
    }

    public FabricUserInfo getUser(String userId) {
        if (!users.containsKey(userId)) {
            FabricUserInfo fabricUserInfo = fabricUserDao.getUser(userId);
            if (null != fabricUserInfo) {
                fabricUserInfo.setMspId(getMspId());
                users.put(fabricUserInfo.getId(), fabricUserInfo);
            }
        }
        return users.getOrDefault(userId, null);
    }

    public FabricUserInfo getUser() {
        if (!users.isEmpty()) {
            return users.entrySet().iterator().next().getValue();
        }
        List<FabricUserInfo> users = getUsers();
        if (null == users || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    public void setUser(FabricUserInfo fabricUserInfo) {
        if (1 == fabricUserDao.insertUser(fabricUserInfo)) {
            if (fabricUserInfo.getAdmin()) {
                adminUsers.put(fabricUserInfo.getId(), fabricUserInfo);
            }
            users.put(fabricUserInfo.getId(), fabricUserInfo);
        }
    }

    public void updateUser(FabricUserInfo fabricUserInfo) {
        if (1 == fabricUserDao.updateUser(fabricUserInfo)) {
            if (fabricUserInfo.getAdmin()) {
                adminUsers.put(fabricUserInfo.getId(), fabricUserInfo);
            }
            users.put(fabricUserInfo.getId(), fabricUserInfo);
        }
    }

    public void deleteUser(String userId) {
        if (1 == fabricUserDao.deleteUser(userId)) {
            if (adminUsers.containsKey(userId)) {
                adminUsers.remove(userId);
            }
            if (users.containsKey(userId)) {
                users.remove(userId);
            }
        }
    }

    public List<PeerInfo> getPeers() {
        List<PeerInfo> peersInfo = peerDao.selectPeersWithCrypto();
        synchronized (peers) {
            peers.clear();
            if (null != peersInfo) {
                for (PeerInfo peerInfo : peersInfo) {
                    peers.put(peerInfo.getId(), peerInfo);
                }
            }
        }
        return peersInfo;
    }

    public PeerInfo getPeer(String peerId) {
        if (!peers.containsKey(peerId)) {
            PeerInfo peerInfo = peerDao.getPeer(peerId);
            if (null != peerInfo) {
                peers.put(peerId, peerInfo);
            }
        }
        return peers.getOrDefault(peerId, null);
    }

    public void setPeer(PeerInfo peerInfo) {
        if (1 == peerDao.insertPeer(peerInfo)) {
            peers.put(peerInfo.getId(), peerInfo);
        }
    }

    public void updatePeer(PeerInfo peerInfo) {
        if (1 == peerDao.updatePeer(peerInfo)) {
            peers.put(peerInfo.getId(), peerInfo);
        }
    }

    public void deletePeer(String peerId) {
        if (1 == peerDao.deletePeer(peerId)) {
            if (peers.containsKey(peerId)) {
                peers.remove(peerId);
            }
        }
    }

    public List<OrdererInfo> getOrderers() {
        List<OrdererInfo> orderersInfo = ordererDao.selectOrderersWithCrypto();
        synchronized (orderers) {
            orderers.clear();
            if (null != orderersInfo) {
                for (OrdererInfo ordererInfo : orderersInfo) {
                    orderers.put(ordererInfo.getId(), ordererInfo);
                }
            }
        }
        return orderersInfo;
    }

    public OrdererInfo getOrderer() {
        if (orderers.isEmpty()) {
            List<OrdererInfo> orderersInfo = getOrderers();
            if (null == orderersInfo || orderersInfo.isEmpty()) {
                return null;
            }else {
                return orderersInfo.get(0);
            }
        }
        return orderers.entrySet().iterator().next().getValue();
    }

    public OrdererInfo getOrderer(String ordererId) {
        if (!orderers.containsKey(ordererId)) {
            OrdererInfo ordererInfo = ordererDao.getOrderer(ordererId);
            if (null != ordererInfo) {
                orderers.put(ordererInfo.getId(), ordererInfo);
            }
        }
        return orderers.getOrDefault(ordererId, null);
    }

    public void setOrderer(OrdererInfo ordererInfo) {
        if (1 == ordererDao.insertOrderer(ordererInfo)) {
            orderers.put(ordererInfo.getId(), ordererInfo);
        }
    }

    public void updateOrderer(OrdererInfo ordererInfo) {
        if (1 == ordererDao.updateOrderer(ordererInfo)) {
            orderers.put(ordererInfo.getId(), ordererInfo);
        }
    }

    public void deleteOrderer(String ordererId) {
        if (1 == ordererDao.deleteOrderer(ordererId)) {
            if (orderers.containsKey(ordererId)) {
                orderers.remove(ordererId);
            }
        }
    }
}
