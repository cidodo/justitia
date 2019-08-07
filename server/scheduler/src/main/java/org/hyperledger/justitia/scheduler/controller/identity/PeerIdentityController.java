package org.hyperledger.justitia.scheduler.controller.identity;

import org.hyperledger.justitia.common.bean.node.PeerInfo;
import org.hyperledger.justitia.common.face.service.identity.NodeService;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.identity.beans.SetPeerBean;
import org.hyperledger.justitia.scheduler.controller.identity.format.FormatData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/identity/peer")
public class PeerIdentityController {
    private final NodeService nodeService;

    @Autowired
    public PeerIdentityController(NodeService nodeService) {
        this.nodeService = nodeService;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean setPeer(SetPeerBean form) {
        PeerInfo peerInfo = FormatData.formatPeerInfo(form);
        nodeService.setPeer(peerInfo);
        return new ResponseBean().success();
    }

    @GetMapping
    public ResponseBean<Collection<PeerInfo>> getPeersInfo() {
        Collection<PeerInfo> peersInfo = nodeService.getPeersInfo();
        return new ResponseBean<Collection<PeerInfo>>().success(peersInfo);
    }

    @PutMapping(value = "/{peerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean update(SetPeerBean form, @PathVariable("peerId") String peerId) {
        form.setName(peerId);
        PeerInfo peerInfo = FormatData.formatPeerInfo(form);
        nodeService.updatePeerInfo(peerInfo);
        return new ResponseBean().success();
    }

    @DeleteMapping("/{peerId}")
    public ResponseBean delete(@PathVariable("peerId") String peerId) {
        nodeService.deletePeer(peerId);
        return new ResponseBean().success();
    }
}
