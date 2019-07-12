package org.hyperledger.justitia.common.face.modules.channel;

import org.hyperledger.justitia.common.face.modules.channel.beans.TaskInfo;

import java.util.List;

public interface ChannelTaskService {
    List<TaskInfo> getTasks();
    TaskInfo getTask(String taskId);
    void channelConfigTaskResponse(String requestId, boolean reject, String reason);
    void submitRequest(String requestId);
    void recallMyRequest(String requestId);
    void deleteTask(String requestId);
}
