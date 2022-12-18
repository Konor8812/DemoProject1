package com.illia.client.http_client;


import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface MyHttpClient {
    ResponseEntity<String> performUploadFileRequest(String url, File file);
    ResponseEntity<Object> performDownloadFileRequest(String url);
}
