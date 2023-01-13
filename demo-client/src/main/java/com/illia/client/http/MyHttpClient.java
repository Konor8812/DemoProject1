package com.illia.client.http;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

public interface MyHttpClient {
    ResponseEntity<String> performUploadFileRequest(String fileName, ByteArrayResource resource, boolean overwrite);
    ResponseEntity<byte[]> performDownloadFileRequest(String fileName);
}
