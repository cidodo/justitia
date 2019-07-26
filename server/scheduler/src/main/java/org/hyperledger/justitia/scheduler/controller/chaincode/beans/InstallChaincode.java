package org.hyperledger.justitia.scheduler.controller.chaincode.beans;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class InstallChaincode {
    @NotEmpty
    private String language;
    @NotEmpty
    private String chaincodeName;
    private String path;
    @NotEmpty
    private String version;
    @NotNull
    private List<String> peersName;
    private MultipartFile chaincodeFile;


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getPeersName() {
        return peersName;
    }

    public void setPeersName(List<String> peersName) {
        this.peersName = peersName;
    }

    public MultipartFile getChaincodeFile() {
        return chaincodeFile;
    }

    public void setChaincodeFile(MultipartFile chaincodeFile) {
        this.chaincodeFile = chaincodeFile;
    }
}
