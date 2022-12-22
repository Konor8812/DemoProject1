package com.illia.server.controller;

import com.illia.server.request_processor.RequestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.*;



@Slf4j
@RestController
@RequestMapping("demo")
public class DemoServerController {

    @Autowired
    RequestProcessor requestProcessor;

    @PostMapping(value = "/uploadFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadFile(@RequestParam(name = "fileName") String fileName,
                                             @RequestPart(required = false) File file) {
        return requestProcessor.proceedSaveFile(fileName, file);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Object> downloadFile(@RequestParam(name = "fileName") String fileName) {
        return requestProcessor.proceedDownloadFile(fileName);
    }


    @GetMapping("/savedFilesAmount")
    public ResponseEntity<Integer> getAmount() {
        return requestProcessor.getFilesAmount();
    }


}
