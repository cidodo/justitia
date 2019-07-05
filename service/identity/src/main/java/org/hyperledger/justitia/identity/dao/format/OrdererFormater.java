package org.hyperledger.justitia.identity.dao.format;

import org.hyperledger.justitia.dao.bean.Orderer;
import org.hyperledger.justitia.identity.service.beans.OrdererInfo;

import java.util.ArrayList;
import java.util.List;

public class OrdererFormater {
    public static OrdererInfo orderer2OrdererInfo(Orderer orderer) {
        if (null == orderer) {
            return null;
        }

        OrdererInfo ordererInfo = new OrdererInfo();
        ordererInfo.setId(orderer.getId());
        ordererInfo.setIp(orderer.getIp());
        ordererInfo.setPort(orderer.getPort());
        ordererInfo.setTlsEnable(orderer.getTlsEnable());
        ordererInfo.setDoubleVerity(orderer.getDoubleVerify());
        if (null != orderer.getContainer()) {
            ordererInfo.setSslTarget(orderer.getContainer().getContainerName());
        }
        ordererInfo.setCrypto(MspFormater.msp2NodeCrypto(orderer.getMsp()));

        return ordererInfo;
    }

    public static List<OrdererInfo> orderers2OrderersInfo(List<Orderer> orderers) {
        if (null == orderers || orderers.isEmpty()) {
            return null;
        }

        List<OrdererInfo> orderersInfo = new ArrayList<>();
        for (Orderer orderer : orderers) {
            OrdererInfo ordererInfo = OrdererFormater.orderer2OrdererInfo(orderer);
            orderersInfo.add(ordererInfo);
        }

        return orderersInfo;
    }

    public static Orderer ordererInfo2Orderre(OrdererInfo ordererInfo, String mspId) {
        if (null == ordererInfo) {
            return null;
        }

        Orderer orderer = new Orderer();

        if (null != ordererInfo.getId()) {
            orderer.setId(ordererInfo.getId());
        }

        if (null != ordererInfo.getIp()) {
            orderer.setIp(ordererInfo.getIp());
        }

        if (null != ordererInfo.getPort()) {
            orderer.setPort(ordererInfo.getPort());
        }

        if (null != ordererInfo.getContainerId()) {
            orderer.setContainerId(ordererInfo.getContainerId());
        }

        if (null != mspId && !mspId.isEmpty()) {
            orderer.setMspId(mspId);
        }

        if (null != ordererInfo.getTlsEnable()) {
            orderer.setTlsEnable(ordererInfo.getTlsEnable());
        }

        if (null != ordererInfo.getDoubleVerity()) {
            orderer.setDoubleVerify(ordererInfo.getDoubleVerity());
        }

        return orderer;
    }
}
