package org.hyperledger.justitia.scheduler.controller.identity.beans;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TlsBean {
    private Boolean doubleVerify;
    private List<String> sslTarget;
    private MultipartFile ca;
    private MultipartFile cert;
    private MultipartFile key;
}
