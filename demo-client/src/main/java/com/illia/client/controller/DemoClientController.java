package com.illia.client.controller;

import com.illia.client.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("demo")
public class DemoClientController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/uploadFile")
    public String uploadFile(@RequestParam(name = "fileName") String fileName,
                             @RequestParam(name = "file", required = false) File file) {
        log.info("Upload file client request {}", fileName);
        var msg = demoService.uploadFile(fileName, file);
        return msg;
    }


    @GetMapping("/downloadFile")
    public String downloadFile(@RequestParam(name = "fileName") String fileName) {
        log.info("Download file client request {}", fileName);
        //

        var file = demoService.downloadFile(fileName);

        //
        var msg = file == null? "File wasn't downloaded, no such file stored on server : " + fileName : "File downloaded " + file.getName();
        return msg;
    }

}
