package org.hyperledger.justitia.scheduler.controller.channel;

import org.hyperledger.justitia.common.bean.channel.ChannelConfigProposal;
import org.hyperledger.justitia.common.face.service.channel.ChannelConfigProposalService;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.channel.beans.ResponseTaskBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/channel")
public class ChannelTaskController {

    private final ChannelConfigProposalService proposalService;

    @Autowired
    public ChannelTaskController(ChannelConfigProposalService proposalService) {
        this.proposalService = proposalService;
    }

    /**
     * 获取全部待处理任务
     */
    @GetMapping("/task")
    public ResponseBean<List<ChannelConfigProposal>> getChannelConfigTask() {
        List<ChannelConfigProposal> proposals = proposalService.getProposals();
        return new ResponseBean<List<ChannelConfigProposal>>().success(proposals);
    }

    /**
     * 获取指定待处理任务的详细信息
     */
    @GetMapping("/task/{taskId}")
    public ResponseBean<ChannelConfigProposal> getChannelConfigTask(@PathVariable("taskId") String taskId) {
        ChannelConfigProposal task = proposalService.getProposal(taskId);
        return new ResponseBean<ChannelConfigProposal>().success(task);
    }

    /**
     * 任务响应
     */
    @PostMapping("/task/response")
    public ResponseBean channelConfigTaskResponse(@RequestBody @Valid ResponseTaskBean body) {
        String taskId = body.getTaskId();
        Boolean reject = body.getReject();
        proposalService.createSignResponse(taskId, reject, body.getReason());
        return new ResponseBean().success();
    }

    @PutMapping("/task/submit/{taskId}")
    public ResponseBean submitRequest(@PathVariable("taskId") String taskId) {
        proposalService.submitProposal(taskId);
        return new ResponseBean().success();
    }

    @PutMapping("/task/recall/{taskId}")
    public ResponseBean recallRequest(@PathVariable("taskId") String taskId) {
        proposalService.recallProposal(taskId);
        return new ResponseBean().success();
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/task/{taskId}")
    public ResponseBean deleteChannelConfigTask(@PathVariable("taskId") String taskId) {
        proposalService.deleteProposal(taskId);
        return new ResponseBean().success();
    }
}
