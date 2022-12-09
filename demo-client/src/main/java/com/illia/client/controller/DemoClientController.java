package com.illia.client.controller;

import com.illia.client.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("demo")
public class DemoClientController {

    @Autowired
    private DemoService demoService;

    @PostMapping(value = "/uploadFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadFile(@RequestParam(name = "fileName") String fileName,
                                             @RequestPart MultipartFile file) {
        log.info("Upload file client request: {}", fileName);
        return demoService.uploadFile(fileName, file);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<File> downloadFile(@RequestParam(name = "fileName") String fileName) {
        log.info("download file client request: {}", fileName);
        return demoService.downloadFile(fileName);
    }
}
