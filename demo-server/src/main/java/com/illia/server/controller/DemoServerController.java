package com.illia.server.controller;

import com.illia.server.request_processor.RequestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("demo")
public class DemoServerController {

    @Autowired
    private RequestProcessor requestProcessor;

    @PostMapping(value = "/uploadFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadFile(@RequestParam(name = "fileName") String fileName,
                                             @RequestHeader(name = "Overwrite", required = false, defaultValue = "false") Boolean overwrite,
                                             @RequestPart(required = false, name = "resource") ByteArrayResource byteArrayResource) {
        return requestProcessor.proceedSaveFile(fileName, byteArrayResource, overwrite);
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
