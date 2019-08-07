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

import org.hyperledger.justitia.common.bean.identity.FabricUser;
import org.hyperledger.justitia.common.bean.identity.Organization;
import org.hyperledger.justitia.common.bean.node.OrdererInfo;
import org.hyperledger.justitia.common.bean.node.PeerInfo;
import org.hyperledger.justitia.identity.dao.FabricUserDao;
import org.hyperledger.justitia.identity.dao.OrdererDao;
import org.hyperledger.justitia.identity.dao.OrganizationDao;
import org.hyperledger.justitia.identity.dao.PeerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdentityConfig {
    private static final Map<String, Organization> organizations = new ConcurrentHashMap<>();
    private static final Map<String, FabricUser> adminUsers = new ConcurrentHashMap<>();
    private static final Map<String, FabricUser> users = new ConcurrentHashMap<>();
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

    public Organization getOrganization() {
        if (IdentityConfig.organizations.isEmpty()) {
            fullOrganizations();
        }
        Iterator<Organization> iterator = organizations.values().iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }

    public Organization getOrganization(String orgId) {
        if (IdentityConfig.organizations.containsKey(orgId)) {
            return IdentityConfig.organizations.get(orgId);
        } else {
            Organization organization = organizationDao.getOrganization(orgId);
            IdentityConfig.organizations.put(organization.getId(), organization);
            return organization;
        }
    }

    private void fullOrganizations() {
        List<Organization> organizations = organizationDao.selectOrganization();
        if (null == organizations || organizations.isEmpty()) {
            return;
        }
        for (Organization organization : organizations) {
            IdentityConfig.organizations.put(organization.getId(), organization);
        }
    }

    public void setOrganization(Organization organization) {
        if (1 == organizationDao.insertOrganization(organization)) {
            IdentityConfig.organizations.put(organization.getId(), organization);
        }
    }

    public void updateOrganization(Organization organization) {
        if (1 == organizationDao.updateOrganization(organization)) {
            IdentityConfig.organizations.put(organization.getId(), organization);
        }
    }

    public void deleteOrganization(final String orgId) {
        if (1 == organizationDao.deleteOrganization(orgId)) {
            IdentityConfig.organizations.remove(orgId);
        }
    }

    public Collection<FabricUser> getUsers() {
        List<FabricUser> fabricUsersInfo = fabricUserDao.selectUser();
        synchronized (this) {
            adminUsers.clear();
            users.clear();
            if (null != fabricUsersInfo) {
                for (FabricUser fabricUserInfo : fabricUsersInfo) {
                    if (fabricUserInfo.getAdmin()) {
                        adminUsers.put(fabricUserInfo.getId(), fabricUserInfo);
                    }
                    users.put(fabricUserInfo.getId(), fabricUserInfo);
                }
            }
        }
        return users.values();
    }

    public FabricUser getAdminUser() {
        if (adminUsers.isEmpty()) {
            List<FabricUser> adminUsersInfo = fabricUserDao.selectAdminsUser();
            synchronized (adminUsers) {
                adminUsers.clear();
                if (null != adminUsersInfo) {
                    for (FabricUser adminUserInfo : adminUsersInfo) {
                        adminUsers.put(adminUserInfo.getId(), adminUserInfo);
                    }
                }
            }
        }
        if (adminUsers.isEmpty()) {
            return null;
        } else {
            return adminUsers.values().iterator().next();
        }
    }

    public FabricUser getUser(String userId) {
        if (!users.containsKey(userId)) {
            FabricUser fabricUserInfo = fabricUserDao.getUser(userId);
            if (null != fabricUserInfo) {
                users.put(fabricUserInfo.getId(), fabricUserInfo);
            }
        }
        return users.getOrDefault(userId, null);
    }

    public FabricUser getUser() {
        if (!users.isEmpty()) {
            return users.entrySet().iterator().next().getValue();
        }
        Collection<FabricUser> users = getUsers();
        if (null == users || users.isEmpty()) {
            return null;
        } else {
            return users.iterator().next();
        }
    }

    public void setUser(FabricUser fabricUserInfo) {
        if (1 == fabricUserDao.insertUser(fabricUserInfo)) {
            if (fabricUserInfo.getAdmin()) {
                adminUsers.put(fabricUserInfo.getId(), fabricUserInfo);
            }
            users.put(fabricUserInfo.getId(), fabricUserInfo);
        }
    }

    public void updateUser(FabricUser fabricUserInfo) {
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
        List<PeerInfo> peersInfo = peerDao.selectPeers();
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
        List<OrdererInfo> orderersInfo = ordererDao.selectOrderers();
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
            } else {
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
