package org.hyperledger.justitia.scheduler.controller.channel;

import org.hyperledger.justitia.common.face.service.channel.ChannelManageService;
import org.hyperledger.justitia.common.bean.channel.ChannelInfo;
import org.hyperledger.justitia.scheduler.utils.MultipartFileUtils;
import org.hyperledger.justitia.scheduler.controller.DownloadHelper;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.channel.beans.AddChannelMemberBean;
import org.hyperledger.justitia.scheduler.controller.channel.beans.CreateChannelBean;
import org.hyperledger.justitia.scheduler.controller.channel.beans.DeleteChannelMemberBean;
import org.hyperledger.justitia.scheduler.controller.channel.beans.JoinChannelBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping(value = "/channel")
public class ChannelManageController {
    private final ChannelManageService manageService;

    @Autowired
    public ChannelManageController(ChannelManageService manageService) {
        this.manageService = manageService;
    }

    @PostMapping
    public ResponseBean createChannel(@RequestBody @Valid CreateChannelBean body) {
        String channelId = body.getChannelId();
        String consortiumName = body.getConsortiumName();
        List<String> peersId = body.getPeersId();
        manageService.createChannel(channelId, consortiumName, peersId);
        return new ResponseBean().success();
    }

    @GetMapping
    public ResponseBean<List<ChannelInfo>> getChannels() {
        List<ChannelInfo> channelsInfo = manageService.getChannelsInfo();
        return new ResponseBean<List<ChannelInfo>>().success(channelsInfo);
    }

    @GetMapping("/{channelId}")
    public ResponseBean<ChannelInfo> getChannel(@PathVariable("channelId") String channelId) {
        ChannelInfo channelInfo = manageService.getChannelInfo(channelId);
        return new ResponseBean<ChannelInfo>().success(channelInfo);
    }

    @PostMapping("/join")
    public ResponseBean joinChannel(@RequestBody @Valid JoinChannelBean body) {
        String channelId = body.getChannelId();
        List<String> peerId = body.getPeerId();
        manageService.peerJoinChannel(channelId, peerId);
        return new ResponseBean().success();
    }

    @GetMapping("/organization/config")
    public ResponseEntity<byte[]> getMemberConfig() {
        InputStream organizationConfig = manageService.getMemberConfig();
        ResponseEntity<byte[]> responseEntity = DownloadHelper.getResponseEntity(organizationConfig, "organization-config.json");
        return responseEntity;
    }

    @PostMapping(value = "/organization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean addMember(AddChannelMemberBean form) throws IOException {
        String channelId = form.getChannelId();
        String description = form.getDescription();
        String orgName = form.getOrgName();
        MultipartFile orgConfigFile = form.getOrgConfig();
        String orgConfig = MultipartFileUtils.readFileAsString(orgConfigFile);
        manageService.addMember(channelId, orgName, orgConfig, description);
        return new ResponseBean().success();
    }

    @DeleteMapping("/organization")
    public ResponseBean deleteMember(@RequestBody DeleteChannelMemberBean body) {
        String channelId = body.getChannelId();
        String orgName = body.getOrgName();
        String description = body.getDescription();
        manageService.deleteMember(channelId, orgName, description);
        return new ResponseBean().success();
    }

    @GetMapping("/msp/{channelId}")
    public ResponseBean<List<String>> getChannelMspIds(@PathVariable("channelId") String channelId) {
        List<String> channelMspId = manageService.getChannelMspId(channelId);
        return new ResponseBean<List<String>>().success(channelMspId);
    }

    @PostMapping("/anchor")
    public ResponseBean addAnchorPeer() {
        manageService.addAnchorPeer();
        return new ResponseBean().success();
    }

    @DeleteMapping("/anchor")
    public ResponseBean deleteAnchorPeer() {
        manageService.deleteAnchorPeer();
        return new ResponseBean().success();
    }
}
