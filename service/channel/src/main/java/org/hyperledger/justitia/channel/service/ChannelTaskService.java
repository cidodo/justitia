package org.hyperledger.justitia.channel.service;

import org.hyperledger.justitia.channel.service.beans.TaskInfo;
import org.hyperledger.justitia.channel.service.beans.TaskSummary;

import java.util.List;

public interface ChannelTaskService {
    List<TaskSummary> getTasks();
    TaskInfo getTask(String taskId);
    void channelConfigTaskResponse(String requestId, boolean reject, String reason);
    void submitRequest(String requestId);
    void recallMyRequest(String requestId);
    void deleteTask(String requestId);
}
