package org.hyperledger.justitia.scheduler.controller;

import org.hyperledger.justitia.identity.service.beans.OrganizationInfo;
import org.hyperledger.justitia.identity.service.read.OrganizationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/context")
public class ContextController {
    private final OrganizationReader organizationReader;

    @Autowired
    public ContextController(OrganizationReader organizationReader) {
        this.organizationReader = organizationReader;
    }

    @GetMapping("/check")
    public ResponseBean checkSystemContext() {
        OrganizationInfo organizationInfo = organizationReader.getOrganizationInfo();
        if (null != organizationInfo) {
            return new ResponseBean().success();
        }
        return new ResponseBean().failure(ResponseBean.CONTEXT_NOT_INIT, "System not initialize");
    }
}
