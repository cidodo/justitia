package org.hyperledger.justitia.scheduler.controller.chaincode;


import org.hyperledger.justitia.chaincode.service.ChaincodeRequestService;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.chaincode.beans.InvokeChaincode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/chaincode")
public class ChaincodeRequestController {
    private final ChaincodeRequestService requestService;

    @Autowired
    public ChaincodeRequestController(ChaincodeRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/test")
    public ResponseBean test() {
        return new ResponseBean().success();
    }

    @PostMapping(value = "/invoke")
    public ResponseBean invoke(@RequestBody @Valid InvokeChaincode body) {
//        requestService.invoke();
        return new ResponseBean().success();
    }

    @PostMapping(value = "/query")
    public ResponseBean query(@RequestBody @Valid InvokeChaincode body) {
//        requestService.query();
        return new ResponseBean().success();
    }

    @PostMapping(value = "/queryInstalled")
    public ResponseBean<List> queryInstalledChaincode() {
//        List chaincodes = requestService.queryInstalledChaincode();
//        return new ResponseBean<List>().success(chaincodes);
        return new ResponseBean<>().success();
    }

    @PostMapping(value = "/queryInstantiated")
    public ResponseBean<List> queryInstantiatedChaincode() {
//        List chaincodes = requestService.queryInstantiatedChaincode();
//        return new ResponseBean<List>().success(chaincodes);
        return new ResponseBean<>().success();
    }
}
