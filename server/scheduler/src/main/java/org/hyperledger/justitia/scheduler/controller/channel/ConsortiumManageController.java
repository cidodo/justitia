package org.hyperledger.justitia.scheduler.controller.channel;

import org.hyperledger.justitia.common.bean.channel.ChannelMember;
import org.hyperledger.justitia.common.face.service.channel.ConsortiumManageService;
import org.hyperledger.justitia.common.bean.channel.ConsortiumInfo;
import org.hyperledger.justitia.scheduler.utils.MultipartFileUtils;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.channel.beans.AddConsortiumMemberBean;
import org.hyperledger.justitia.scheduler.controller.channel.beans.DeleteConsortiumMemberBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/consortium")
public class ConsortiumManageController {

    private final ConsortiumManageService manageService;

    @Autowired
    public ConsortiumManageController(ConsortiumManageService manageService) {
        this.manageService = manageService;
    }

    @GetMapping("/{ordererName}")
    public ResponseBean<List<ConsortiumInfo>> getConsortiums(@PathVariable("ordererName") String ordererName) {
        List<ConsortiumInfo> consortiums = manageService.getConsortiums(ordererName);
        return new ResponseBean<List<ConsortiumInfo>>().success(consortiums);
    }


    @PostMapping(value = "/organization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean addConsortiumMember(AddConsortiumMemberBean form) throws IOException {
        String consortiumName = form.getConsortiumName();
        String ordererName = form.getOrdererName();
        String orgName = form.getOrgName();
        MultipartFile orgConfigFile = form.getOrgConfig();
        String orgConfig = MultipartFileUtils.readFileAsString(orgConfigFile);
        ChannelMember member = new ChannelMember(orgName);
        member.setMemberConfig(orgConfig);
        manageService.addConsortiumMember(ordererName, consortiumName, member);
        return new ResponseBean().success();
    }

    @DeleteMapping("/organization")
    public ResponseBean deleteConsortiumMember(@RequestBody @Valid DeleteConsortiumMemberBean body) {
        String ordererName = body.getOrdererName();
        String consortium = body.getConsortium();
        String orgName = body.getOrgName();
        manageService.deleteConsortiumMember(ordererName, consortium, orgName);
        return new ResponseBean().success();
    }
}
