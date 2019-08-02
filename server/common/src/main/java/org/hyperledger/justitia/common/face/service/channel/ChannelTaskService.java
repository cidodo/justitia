package org.hyperledger.justitia.common.face.service.channel;

import org.hyperledger.justitia.common.bean.channel.ChannelConfigTask;

import java.util.List;

public interface ChannelTaskService {
    List<ChannelConfigTask> getTasks();
    ChannelConfigTask getTask(String taskId);
    void channelConfigTaskResponse(String requestId, boolean reject, String reason);
    void submitRequest(String requestId);
    void recallMyRequest(String requestId);
    void deleteTask(String requestId);
}
