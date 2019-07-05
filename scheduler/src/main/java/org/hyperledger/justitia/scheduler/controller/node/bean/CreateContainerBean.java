package org.hyperledger.justitia.scheduler.controller.node.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.Map;

@Data
public class CreateContainerBean {
    protected String hostName;
    protected String image;
    protected String tag;
    protected String workingDir;
    private String containerName;
    protected Map<String, String> env;
    protected String cmd;
    private Map<Integer, Integer> exposedPorts;  //tlsKey:容器内部端口, value:映射出来的端口
    private String networkMode;
    protected Map<String, String> volumes;         //tlsKey:本地挂在路径或卷, value:容器内部路径
    private String[] extraHosts;

    public String getExposedPortsString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(exposedPorts);
    }

}
