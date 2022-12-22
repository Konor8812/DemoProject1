package com.illia.client.http_client;


import org.springframework.http.ResponseEntity;

import java.io.File;

public interface MyHttpClient {
    ResponseEntity<String> performUploadFileRequest(String url, File file);
    ResponseEntity<byte[]> performDownloadFileRequest(String url);
}
