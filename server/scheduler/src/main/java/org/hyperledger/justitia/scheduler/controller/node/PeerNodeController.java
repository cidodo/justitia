package org.hyperledger.justitia.scheduler.controller.node;

import org.hyperledger.justitia.common.face.service.node.PeerService;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.node.bean.CreatePeerBean;
import org.hyperledger.justitia.scheduler.controller.identity.beans.SetNodeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/node/peer")
public class PeerNodeController {

    private final PeerService peerService;

    @Autowired
    public PeerNodeController(PeerService peerService) {
        this.peerService = peerService;
    }

    @PostMapping()
    public ResponseBean createPeer(@RequestBody @Valid CreatePeerBean body) {
//        String requester = tokenManager.getRequester(request).getUserId();
//        DockerService.ChangeContainerStatusResult result = service.createPeer(requester, body);
//        if (result.isSuccess()) {
//            return new Response().success("Peer节点创建成功");
//        } else {
//            return new Response().failure(result.getErrMsg(), result.getLog());
//        }
        peerService.createPeer();
        return new ResponseBean().success();
    }


    @DeleteMapping("/{peerId}")
    public ResponseBean deletePeer(@PathVariable("peerId") String peerId) {
        peerService.deletePeer(peerId);
        return new ResponseBean().success();
    }

    @PutMapping("/{peerId}")
    public ResponseBean updatePeerInfo(@PathVariable("peerId") String peerId, @RequestBody SetNodeBean body) {
        peerService.updatePeerInfo();
        return new ResponseBean().success();
    }

    @PutMapping("/{peerId}/{oper}")
    public ResponseBean changeContainerStatus(@PathVariable("peerId") String peerId, @PathVariable("oper") String oper) {
//        DockerService.ChangeContainerStatusResult result = service.changeContainerStatus(peerName, DockerService.ContainerOper.fromString(oper));
//        if (result.isSuccess()) {
//            return new Response().success("Peer节点" + oper + "成功");
//        } else {
//            return new Response().failure(result.getErrMsg(), result.getLog());
//        }
        return new ResponseBean().success();
    }

    @GetMapping()
    public ResponseBean<List> getPeers() {
        List peersInfo = peerService.getPeersInfo();
        return new ResponseBean<List>().success(peersInfo);
    }
}
