package com.illia.server.request_processor;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface RequestProcessor {

    ResponseEntity<Object> proceedDownloadFile(String fileName);
    ResponseEntity<String> proceedSaveFile(String fileName, ByteArrayResource file, boolean overwrite);
    ResponseEntity<Integer> getFilesAmount();
}
