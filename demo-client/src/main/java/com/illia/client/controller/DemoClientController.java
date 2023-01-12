package com.illia.client.controller;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.FileTransferService;
import com.illia.client.service.QueryProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("demo")
public class DemoClientController {

    @Autowired
    private FileTransferService fileTransferService;


    @PostMapping(value = "/uploadFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadFile(@RequestParam(name = "fileName") String fileName,
                                             @RequestHeader(name = "Overwrite", required = false, defaultValue = "false") Boolean overwrite,
                                             @RequestPart MultipartFile multipartFile) {
        return fileTransferService.uploadFile(fileName, multipartFile, overwrite);
    }


    @GetMapping("/downloadFile")
    public ResponseEntity<String> downloadFile(@RequestParam(name = "fileName") String fileName,
                                               @RequestParam(name = "overwrite", required = false, defaultValue = "false") Boolean overwrite) {
        return fileTransferService.downloadFile(fileName, overwrite);
    }

    @GetMapping("/deleteFile")
    public ResponseEntity<String> deleteFile(@RequestParam(name = "fileName") String fileName) {
        return fileTransferService.deleteFile(fileName);
    }
}
