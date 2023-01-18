package com.illia.client.controller;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.QueryRequestEntity;
import com.illia.client.service.FileTransferService;
import com.illia.client.service.QueryProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("demo")
public class DemoClientController {

    @Autowired
    private FileTransferService fileTransferService;

    @Autowired
    private QueryProcessingService queryProcessingService;

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

    @GetMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> performOperation(@RequestBody QueryRequestEntity entity) {
        var response = queryProcessingService.performOperation(entity);
        // still unsure where to form ResponseEntities, have questions about it
        return ResponseEntity.ok("ok");
    }

}
