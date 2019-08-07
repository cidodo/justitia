package org.hyperledger.justitia.scheduler.controller;

import org.hyperledger.justitia.common.bean.identity.Organization;
import org.hyperledger.justitia.common.face.service.identity.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/context")
public class ContextController {
    private final OrganizationService organizationService;

    @Autowired
    public ContextController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/check")
    public ResponseBean checkSystemContext() {
        Organization organizationInfo = organizationService.getOrganization();
        if (null != organizationInfo) {
            return new ResponseBean().success();
        }
        return new ResponseBean().failure(ResponseBean.CONTEXT_NOT_INIT, "System not initialize");
    }
}
