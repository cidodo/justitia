package org.hyperledger.justitia.scheduler.controller.identity;

import org.hyperledger.justitia.common.face.service.identity.read.OrganizationReader;
import org.hyperledger.justitia.common.face.service.identity.write.OrganizationWriter;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.identity.beans.SetOrganizationBean;
import org.hyperledger.justitia.scheduler.controller.identity.format.FormatData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/identity/organization")
public class OrganizationController {

    @Autowired
    private OrganizationReader reader;
    @Autowired
    private OrganizationWriter writer;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean SetOrganization(SetOrganizationBean form) {
        OrganizationInfo organizationInfo = FormatData.formatOrganization(form);
        writer.setOrganization(organizationInfo);
        return new ResponseBean().success();
    }


    @GetMapping("/base")
    public ResponseBean<OrganizationInfo> getOrganizationInfo(){
        OrganizationInfo organizationInfo = reader.getOrganizationInfo();
        return new ResponseBean<OrganizationInfo>().success(organizationInfo);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean updateOrganization(SetOrganizationBean form) {
        OrganizationInfo organizationInfo = FormatData.formatOrganization(form);
        writer.updateOrganizationInfo(organizationInfo);
        return new ResponseBean().success();
    }

    @DeleteMapping
    public ResponseBean delete() {
        writer.deleteOrganization();
        return new ResponseBean().success();
    }

}
