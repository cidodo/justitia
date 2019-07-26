package org.hyperledger.justitia.scheduler.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;

public class DownloadHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(DownloadHelper.class);

    public static ResponseEntity<byte[]> getResponseEntity(InputStream is, String fileName) {
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.ok()
                .header("Content-Disposition", "attachment;fileName=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            return bodyBuilder.body(IOUtils.toByteArray(is));     //fixme 可以直接把is放进去吗
        } catch (IOException e) {
            LOGGER.error("InputStream to bytes error.",e);
            return bodyBuilder.body(null);     //fixme 可以直接把is放进去吗
        }
    }
}
