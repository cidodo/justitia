package org.hyperledger.justitia.channel.service.impl;

import org.hyperledger.justitia.channel.service.ChannelTaskService;
import org.hyperledger.justitia.channel.service.beans.TaskInfo;
import org.hyperledger.justitia.channel.service.beans.TaskSummary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelTaskServiceImpl implements ChannelTaskService {
    @Override
    public List<TaskSummary> getTasks() {
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
