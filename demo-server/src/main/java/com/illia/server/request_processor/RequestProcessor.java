package com.illia.server.request_processor;


import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface RequestProcessor {

    ResponseEntity<Object> proceedDownloadFile(String fileName);
    ResponseEntity<String> proceedSaveFile(String fileName, File file);
    ResponseEntity<Integer> getFilesAmount();
}
