package com.illia.client.controller;

import com.illia.client.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("demo")
public class DemoClientController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/uploadFile")
    public String uploadFile(@RequestParam(name = "fileName", required = false) String fileName,
                           @RequestParam(name = "file", required = false) File file) {
        if(fileName == null){
            fileName = "IMDb_Movie_Database.csv";
        }
        if (file == null) {
            file = new File(fileName);
        }

        demoService.uploadFile(fileName, file);
        return "File uploaded";
    }

    @GetMapping("/downloadFile")
    public String downloadFile(@RequestParam(name = "fileName", required = false) String fileName) {
        if(fileName == null){
            fileName = "IMDb_Movie_Database.csv";
        }

        return "File downloaded";
    }

    @GetMapping("/countRows")
    public String countRows(@RequestParam(name = "fileName", required = false) String fileName) throws IOException {
        if(fileName == null){
            fileName = "IMDb_Movie_Database.csv";
        }
        File file = new File(fileName);
        var rowsAmount = demoService.getRowsAmount(file);
        return Long.toString(rowsAmount);
    }


}
