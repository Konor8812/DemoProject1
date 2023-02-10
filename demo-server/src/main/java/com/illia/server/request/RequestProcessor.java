package com.illia.server.request;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

public interface RequestProcessor {

  ResponseEntity<Object> proceedDownloadFile(String fileName);

  ResponseEntity<String> proceedSaveFile(String fileName, ByteArrayResource file, boolean overwrite);

  ResponseEntity<Integer> getFilesAmount();
}
