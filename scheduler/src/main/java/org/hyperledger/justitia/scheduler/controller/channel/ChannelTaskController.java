package org.hyperledger.justitia.scheduler.controller.channel;

import org.hyperledger.justitia.channel.service.ChannelTaskService;
import org.hyperledger.justitia.channel.service.beans.TaskInfo;
import org.hyperledger.justitia.channel.service.beans.TaskSummary;
import org.hyperledger.justitia.channel.service.impl.ChannelTaskServiceImpl;
import org.hyperledger.justitia.scheduler.controller.ResponseBean;
import org.hyperledger.justitia.scheduler.controller.channel.beans.ResponseTaskBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/channel")
public class ChannelTaskController {

    private final ChannelTaskService taskService;

    @Autowired
    public ChannelTaskController(ChannelTaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 获取全部待处理任务
     */
    @GetMapping("/task")
    public ResponseBean<List<TaskSummary>> getChannelConfigTask() {
        List<TaskSummary> tasks = taskService.getTasks();
        return new ResponseBean<List<TaskSummary>>().success(tasks);
    }

    /**
     * 获取指定待处理任务的详细信息
     */
    @GetMapping("/task/{taskId}")
    public ResponseBean<TaskInfo> getChannelConfigTask(@PathVariable("taskId") String taskId) {
        TaskInfo task = taskService.getTask(taskId);
        return new ResponseBean<TaskInfo>().success(task);
    }

    /**
     * 任务响应
     */
    @PostMapping("/task/response")
    public ResponseBean channelConfigTaskResponse(@RequestBody @Valid ResponseTaskBean body) {
        String taskId = body.getTaskId();
        Boolean reject = body.getReject();
        taskService.channelConfigTaskResponse(taskId, reject, body.getReason());
        return new ResponseBean().success();
    }

    @PutMapping("/task/submit/{taskId}")
    public ResponseBean submitRequest(@PathVariable("taskId") String taskId) {
        taskService.submitRequest(taskId);
        return new ResponseBean().success();
    }

    @PutMapping("/task/recall/{taskId}")
    public ResponseBean recallRequest(@PathVariable("taskId") String taskId) {
        taskService.recallMyRequest(taskId);
        return new ResponseBean().success();
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/task/{taskId}")
    public ResponseBean deleteChannelConfigTask(@PathVariable("taskId") String taskId) {
        taskService.deleteTask(taskId);
        return new ResponseBean().success();
    }
}
