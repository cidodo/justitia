package org.hyperledger.justitia.dao.bean;

import java.io.Serializable;

public class FabricCa implements Serializable {
    /**
     * 。
    * <p> column ==>id</p>
     */
    private String id;

    /**
     * fabric_ca。
    * <p> table ==>FabricCa</p>
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
}