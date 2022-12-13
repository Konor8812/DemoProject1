package com.illia.server.controller;


import com.illia.server.request_processor.RequestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;


@Slf4j
@RestController
@RequestMapping("demo")
public class DemoServerController {

    @Autowired
    RequestProcessor requestProcessor;

    @PostMapping(value = "/uploadMultipartFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadMultipartFile(@RequestParam(name = "fileName") String fileName,
                                             @RequestPart MultipartFile file) {
        log.info("Upload multipart file server request: {}", fileName);
        return requestProcessor.proceedSaveFile(fileName, file);
    }

    @PostMapping(value = "/uploadFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadFile(@RequestParam(name = "fileName") String fileName,
                                             @RequestPart File file) {
        log.info("Upload file server request: {}", fileName);
        return requestProcessor.proceedSaveFile(fileName, file);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Object> downloadFile(@RequestParam(name = "fileName") String fileName) {
        log.info("Download file server request: {}", fileName);
        return requestProcessor.proceedDownloadFile(fileName);
    }


    @GetMapping("/savedFilesAmount")
    public ResponseEntity<Integer> getAmount() {
        log.info("Saved files amount request ");
        return requestProcessor.getFilesAmount();
    }
}
