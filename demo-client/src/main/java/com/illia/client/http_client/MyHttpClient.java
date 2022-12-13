package com.illia.client.http_client;


import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface MyHttpClient {
    ResponseEntity<String> uploadFile(String fileName, MultipartFile file);
    ResponseEntity<Object> downloadFile(String fileName);
    void setPrefix(String prefix);
}
