package org.hyperledger.justitia.scheduler.controller.identity;

import org.hyperledger.justitia.service.face.identity.bean.PeerInfo;
import org.hyperledger.justitia.service.face.identity.read.NodeReader;
import org.hyperledger.justitia.service.face.identity.write.NodeWriter;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.identity.beans.SetPeerBean;
import org.hyperledger.justitia.scheduler.controller.identity.format.FormatData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/identity/peer")
public class PeerIdentityController {
    private final NodeReader nodeReader;
    private final NodeWriter nodeWriter;

    @Autowired
    public PeerIdentityController(NodeReader nodeReader, NodeWriter nodeWriter) {
        this.nodeReader = nodeReader;
        this.nodeWriter = nodeWriter;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean setPeer(SetPeerBean form) {
        PeerInfo peerInfo = FormatData.formatPeerInfo(form);
        nodeWriter.setPeer(peerInfo);
        return new ResponseBean().success();
    }

    @GetMapping
    public ResponseBean<List<PeerInfo>> getPeersInfo() {
        List<PeerInfo> peersInfo = nodeReader.getPeersInfo();
        return new ResponseBean<List<PeerInfo>>().success(peersInfo);
    }

    @PutMapping(value = "/{peerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseBean update(SetPeerBean form, @PathVariable("peerId") String peerId) {
        form.setName(peerId);
        PeerInfo peerInfo = FormatData.formatPeerInfo(form);
        nodeWriter.updatePeerInfo(peerInfo);
        return new ResponseBean().success();
    }

    @DeleteMapping("/{peerId}")
    public ResponseBean delete(@PathVariable("peerId") String peerId) {
        nodeWriter.deletePeer(peerId);
        return new ResponseBean().success();
    }
}
