package org.hyperledger.justitia.scheduler.controller.identity;

import org.hyperledger.justitia.identity.service.beans.OrdererInfo;
import org.hyperledger.justitia.identity.service.read.NodeReader;
import org.hyperledger.justitia.identity.service.write.NodeWriter;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.identity.beans.SetOrdererBean;
import org.hyperledger.justitia.scheduler.controller.identity.format.FormatData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/identity/orderer")
public class OrdererIdentityController {
    private final NodeReader nodeReader;
    private final NodeWriter nodeWriter;

    @Autowired
    public OrdererIdentityController(NodeReader nodeReader, NodeWriter nodeWriter) {
        this.nodeReader = nodeReader;
        this.nodeWriter = nodeWriter;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean setOrderer(SetOrdererBean form) {
        OrdererInfo ordererInfo = FormatData.formatOrdererInfo(form);
        nodeWriter.setOrderer(ordererInfo);
        return new ResponseBean().success();
    }

    @GetMapping("/{ordererId}")
    public ResponseBean<OrdererInfo> getOrdererInfo(@PathVariable("ordererId") String ordererId) {
        OrdererInfo ordererInfo = nodeReader.getOrdererInfo(ordererId);
        return new ResponseBean<OrdererInfo>().success(ordererInfo);
    }

    @GetMapping
    public ResponseBean<List<OrdererInfo>> getOrderersInfo() {
        List<OrdererInfo> orderersInfo = nodeReader.getOrderersInfo();
        return new ResponseBean<List<OrdererInfo>>().success(orderersInfo);
    }

    @PutMapping(value = "/{ordererId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean updateOrderer(SetOrdererBean form, @PathVariable("ordererId") String ordererId) {
        form.setName(ordererId);
        OrdererInfo ordererInfo = FormatData.formatOrdererInfo(form);
        nodeWriter.updateOrdererInfo(ordererInfo);
        return new ResponseBean().success();
    }

    @DeleteMapping("/{ordererId}")
    public ResponseBean delete(@PathVariable("ordererId") String ordererId) {
        nodeWriter.deleteOrderer(ordererId);
        return new ResponseBean().success();
    }
}
