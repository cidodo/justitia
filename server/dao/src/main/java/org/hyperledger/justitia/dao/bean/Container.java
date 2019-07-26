package org.hyperledger.justitia.dao.bean;

import java.io.Serializable;

public class Container implements Serializable {
    /**
     * 。
    * <p> column ==>id</p>
     */
    private String id;

    /**
     * 。
    * <p> column ==>host_id</p>
     */
    private String hostId;

    /**
     * 。
    * <p> column ==>container_name</p>
     */
    private String containerName;

    /**
     * 。
    * <p> column ==>image</p>
     */
    private String image;

    /**
     * 。
    * <p> column ==>tag</p>
     */
    private String tag;

    /**
     * 。
    * <p> column ==>working_dir</p>
     */
    private String workingDir;

    /**
     * 。
    * <p> column ==>network_mode</p>
     */
    private String networkMode;

    /**
     * 。
    * <p> column ==>port_map</p>
     */
    private String portMap;

    /**
     * 。
    * <p> column ==>deployer</p>
     */
    private String deployer;

    /**
     * 。
    * <p> column ==>volumes</p>
     */
    private String volumes;

    /**
     * container。
    * <p> table ==>Container</p>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取 。
     * @return {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * 设置 。
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取 。
     * @return {@link #hostId}
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * 设置 。
     * @param hostId 
     */
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    /**
     * 获取 。
     * @return {@link #containerName}
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * 设置 。
     * @param containerName 
     */
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    /**
     * 获取 。
     * @return {@link #image}
     */
    public String getImage() {
        return image;
    }

    /**
     * 设置 。
     * @param image 
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * 获取 。
     * @return {@link #tag}
     */
    public String getTag() {
        return tag;
    }

    /**
     * 设置 。
     * @param tag 
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * 获取 。
     * @return {@link #workingDir}
     */
    public String getWorkingDir() {
        return workingDir;
    }

    /**
     * 设置 。
     * @param workingDir 
     */
    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    /**
     * 获取 。
     * @return {@link #networkMode}
     */
    public String getNetworkMode() {
        return networkMode;
    }

    /**
     * 设置 。
     * @param networkMode 
     */
    public void setNetworkMode(String networkMode) {
        this.networkMode = networkMode;
    }

    /**
     * 获取 。
     * @return {@link #portMap}
     */
    public String getPortMap() {
        return portMap;
    }

    /**
     * 设置 。
     * @param portMap 
     */
    public void setPortMap(String portMap) {
        this.portMap = portMap;
    }

    /**
     * 获取 。
     * @return {@link #deployer}
     */
    public String getDeployer() {
        return deployer;
    }

    /**
     * 设置 。
     * @param deployer 
     */
    public void setDeployer(String deployer) {
        this.deployer = deployer;
    }

    /**
     * 获取 。
     * @return {@link #volumes}
     */
    public String getVolumes() {
        return volumes;
    }

    /**
     * 设置 。
     * @param volumes 
     */
    public void setVolumes(String volumes) {
        this.volumes = volumes;
    }
}