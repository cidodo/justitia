package org.hyperledger.justitia.channel.dao;

import org.hyperledger.justitia.common.bean.channel.ChannelConfigTask;
import org.springframework.stereotype.Service;

@Service
public class ChannelConfigTaskDao {
    public ChannelConfigTask getTask(String requestId) {
        return null;
    }

    public int updateTaskStatus(String requestId, String status) {
        return 0;
    }
}
