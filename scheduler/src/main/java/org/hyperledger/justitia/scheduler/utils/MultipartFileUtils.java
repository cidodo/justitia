package org.hyperledger.justitia.scheduler.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class MultipartFileUtils  {
    public static String readFileAsString(MultipartFile file) throws IOException {
        if (file == null) return null;

        InputStream inputStream = file.getInputStream();
        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[1024];
        int i = 0;
        while ((i = inputStream.read(bytes)) != -1) {
            sb.append(new String(bytes, 0 ,i));
        }
        return sb.toString();
    }
}
