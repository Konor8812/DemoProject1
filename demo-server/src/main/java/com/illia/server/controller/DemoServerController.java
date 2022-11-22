package com.illia.server.controller;


import com.illia.server.request_processor.RequestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;


@Slf4j
@RestController
@RequestMapping("demo")
public class DemoServerController {

    @Autowired
    RequestProcessor requestProcessor;

    @PostMapping(value = "/uploadFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFile(@RequestParam(name = "fileName") String fileName,
                             @RequestPart File file) {
        log.info("Upload file server request {} ", fileName);
        requestProcessor.proceedSaveFile(fileName, file);
        return "File uploaded";
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<File> downloadFile(@RequestParam(name = "fileName") String fileName) {
        log.info("Download file server request {}", fileName);
        var file = requestProcessor.proceedDownloadFile(fileName);
        if (file == null) {
            log.info("File {} wasn't found", fileName);
        }
        return ResponseEntity.ok().body(file);
    }

    @GetMapping("/savedFilesAmount")
    public int getAmount() {
        var amount = requestProcessor.getFilesAmount();
        log.info("Saved files amount: {}", amount);
        return amount;
    }
}
