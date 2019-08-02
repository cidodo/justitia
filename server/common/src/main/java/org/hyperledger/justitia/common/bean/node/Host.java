package org.hyperledger.justitia.common.bean.node;

import java.io.Serializable;

public class Host implements Serializable {
    /**
     * 。
    * <p> column ==>id</p>
     */
    private String id;

    /**
     * 。
    * <p> column ==>protocol</p>
     */
    private String protocol;

    /**
     * 。
    * <p> column ==>ip</p>
     */
    private String ip;

    /**
     * 。
    * <p> column ==>port</p>
     */
    private Integer port;

    /**
     * 。
    * <p> column ==>tls_enable</p>
     */
    private Boolean tlsEnable;

    /**
     * 。
    * <p> column ==>cert_path</p>
     */
    private String certPath;

    /**
     * host。
    * <p> table ==>Host</p>
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
     * @return {@link #protocol}
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * 设置 。
     * @param protocol 
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * 获取 。
     * @return {@link #ip}
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置 。
     * @param ip 
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取 。
     * @return {@link #port}
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 设置 。
     * @param port 
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 获取 。
     * @return {@link #tlsEnable}
     */
    public Boolean getTlsEnable() {
        return tlsEnable;
    }

    /**
     * 设置 。
     * @param tlsEnable 
     */
    public void setTlsEnable(Boolean tlsEnable) {
        this.tlsEnable = tlsEnable;
    }

    /**
     * 获取 。
     * @return {@link #certPath}
     */
    public String getCertPath() {
        return certPath;
    }

    /**
     * 设置 。
     * @param certPath 
     */
    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }
}