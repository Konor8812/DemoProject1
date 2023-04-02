package com.illia.client.http;


import com.illia.client.service.file.FileHandlingException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

public interface MyHttpClient {

  ResponseEntity<String> performUploadFileRequest(String fileName, ByteArrayResource resource, boolean overwrite) throws FileHandlingException;

  ResponseEntity<byte[]> performDownloadFileRequest(String fileName) throws FileHandlingException;

  ResponseEntity<String> getAllSavedFiles();
}
