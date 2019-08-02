package org.hyperledger.justitia.channel.service;

import org.hyperledger.justitia.common.face.service.channel.ChannelTaskService;
import org.hyperledger.justitia.common.face.service.fabric.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelTaskServiceImpl implements ChannelTaskService {
    private final NetworkService networkService;

    @Autowired
    public ChannelTaskServiceImpl(NetworkService networkService) {
        this.networkService = networkService;
    }

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
        networkService.resetNetwork();
    }

    @Override
    public void recallMyRequest(String requestId) {

    }

    @Override
    public void deleteTask(String requestId) {

    }
}
