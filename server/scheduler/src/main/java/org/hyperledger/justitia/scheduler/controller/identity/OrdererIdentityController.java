package org.hyperledger.justitia.scheduler.controller.identity;

import org.hyperledger.justitia.common.bean.node.OrdererInfo;
import org.hyperledger.justitia.common.face.service.identity.NodeService;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.identity.beans.SetOrdererBean;
import org.hyperledger.justitia.scheduler.controller.identity.format.FormatData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/identity/orderer")
public class OrdererIdentityController {
    private final NodeService nodeService;

    @Autowired
    public OrdererIdentityController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean setOrderer(SetOrdererBean form) {
        OrdererInfo ordererInfo = FormatData.formatOrdererInfo(form);
        nodeService.setOrderer(ordererInfo);
        return new ResponseBean().success();
    }

    @GetMapping("/{ordererId}")
    public ResponseBean<OrdererInfo> getOrdererInfo(@PathVariable("ordererId") String ordererId) {
        OrdererInfo ordererInfo = nodeService.getOrdererInfo(ordererId);
        return new ResponseBean<OrdererInfo>().success(ordererInfo);
    }

    @GetMapping
    public ResponseBean<Collection<OrdererInfo>> getOrderersInfo() {
        Collection<OrdererInfo> orderersInfo = nodeService.getOrderersInfo();
        return new ResponseBean<Collection<OrdererInfo>>().success(orderersInfo);
    }

    @PutMapping(value = "/{ordererId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean updateOrderer(SetOrdererBean form, @PathVariable("ordererId") String ordererId) {
        form.setName(ordererId);
        OrdererInfo ordererInfo = FormatData.formatOrdererInfo(form);
        nodeService.updateOrdererInfo(ordererInfo);
        return new ResponseBean().success();
    }

    @DeleteMapping("/{ordererId}")
    public ResponseBean delete(@PathVariable("ordererId") String ordererId) {
        nodeService.deleteOrderer(ordererId);
        return new ResponseBean().success();
    }
}
