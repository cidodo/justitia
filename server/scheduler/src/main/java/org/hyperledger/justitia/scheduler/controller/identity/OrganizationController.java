package org.hyperledger.justitia.scheduler.controller.identity;

import org.hyperledger.justitia.common.bean.identity.Organization;
import org.hyperledger.justitia.common.face.service.identity.OrganizationService;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.identity.beans.SetOrganizationBean;
import org.hyperledger.justitia.scheduler.controller.identity.format.FormatData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/identity/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean SetOrganization(SetOrganizationBean form) {
        Organization organizationInfo = FormatData.formatOrganization(form);
        organizationService.setOrganization(organizationInfo);
        return new ResponseBean().success();
    }


    @GetMapping("/base")
    public ResponseBean<Organization> getOrganizationInfo(){
        Organization organization = organizationService.getOrganization();
        return new ResponseBean<Organization>().success(organization);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean updateOrganization(SetOrganizationBean form) {
        Organization organization = FormatData.formatOrganization(form);
        organizationService.updateOrganizationInfo(organization);
        return new ResponseBean().success();
    }

    @DeleteMapping
    public ResponseBean delete() {
        organizationService.deleteOrganization();
        return new ResponseBean().success();
    }

}
