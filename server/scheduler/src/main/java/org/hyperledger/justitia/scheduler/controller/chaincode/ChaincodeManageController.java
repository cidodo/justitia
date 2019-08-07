package org.hyperledger.justitia.scheduler.controller.chaincode;

import org.hyperledger.justitia.common.face.service.chaincode.ChaincodeManageService;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.chaincode.beans.InstallChaincode;
import org.hyperledger.justitia.scheduler.controller.chaincode.beans.InstantiateChaincode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/chaincode")
public class ChaincodeManageController {
    private final ChaincodeManageService manageService;

    @Autowired
    public ChaincodeManageController(ChaincodeManageService manageService) {
        this.manageService = manageService;
    }

    @PostMapping(value = "/install", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean install(@RequestBody @Valid InstallChaincode body) {
//        InputStream inputStream = body.getChaincodeFile().getInputStream();
//        manageService.install();
        return new ResponseBean().success();
    }

    @PostMapping(value = "/instantiate")
    public ResponseBean instantiate(@RequestBody @Valid InstantiateChaincode body) {
//        manageService.instantiate();
        return new ResponseBean().success();
    }

    @PutMapping(value = "/upgrade")
    public ResponseBean upgrade(@RequestBody @Valid InstantiateChaincode body) {
//        manageService.upgrade();
        return new ResponseBean().success();
    }
}
