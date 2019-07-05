package org.hyperledger.justitia.scheduler.controller.bdc;

import org.hyperledger.justitia.scheduler.controller.DownloadHelper;
import org.hyperledger.justitia.scheduler.controller.bdc.bean.AddMemberBean;
import org.hyperledger.justitia.scheduler.service.bdc.BDCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/bdc")
public class BDCController {
    private final BDCService bdcService;

    @Autowired
    public BDCController(BDCService bdcService) {
        this.bdcService = bdcService;
    }

    @PostMapping("/member")
    public ResponseEntity<byte[]> generateIdentityAndAddMember(@RequestBody AddMemberBean body) {
        String channelId = body.getChannelId();
        String identity = body.getIdentity();
        String orgName = body.getOrgName();
        String orgMspId = body.getOrgMspId();
        int peerCount = body.getPeerCount();
        int userCount = body.getUserCount();

        InputStream inputStream = bdcService.generateIdentityAndAddMember(channelId, orgName, orgMspId, identity, userCount, peerCount);
        return DownloadHelper.getResponseEntity(inputStream, "crypto.zip");
    }

    @PostMapping("/organization")
    public ResponseEntity<byte[]> generateIdentity(@RequestBody AddMemberBean body) {
        String identity = body.getIdentity();
        String orgName = body.getOrgName();
        String orgMspId = body.getOrgMspId();
        int peerCount = body.getPeerCount();
        int userCount = body.getUserCount();

        InputStream inputStream = bdcService.generateIdentityIs(orgName, identity, userCount, peerCount);
        return DownloadHelper.getResponseEntity(inputStream, "crypto.zip");
    }
}
