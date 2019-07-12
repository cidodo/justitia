package org.hyperledger.justitia.channel.service;

import org.hyperledger.justitia.common.face.modules.channel.ChannelTaskService;
import org.hyperledger.justitia.common.face.modules.channel.beans.TaskInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelTaskServiceImpl implements ChannelTaskService {
    @Override
    public List<TaskInfo> getTasks() {
        return null;
    }

    @Override
    public TaskInfo getTask(String taskId) {
        return null;
    }

    @Override
    public void channelConfigTaskResponse(String requestId, boolean reject, String reason) {

    }

    @Override
    public void submitRequest(String requestId) {

    }

    @Override
    public void recallMyRequest(String requestId) {

    }

    @Override
    public void deleteTask(String requestId) {

    }
}
