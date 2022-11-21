package com.illia.server.controller;

import com.illia.server.file_holder.FileHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("demo")
public class DemoServerController {

    @Autowired
    private FileHolder fileHolder;


//    @PostMapping("/uploadFile")
    @GetMapping("/uploadFile")
    public String uploadFile(@RequestParam(name = "fileName", required = false) String fileName,
                           @RequestParam(name = "file", required = false) File file) {
        if(fileName == null){
            fileName = "IMDb_Movie_Database.csv";
        }
        if (file == null) {
            file = new File(fileName);
        }

        fileHolder.saveFile(fileName, file);
        return "File uploaded";
    }

    @GetMapping("/downloadFile")
    public File downloadFile(@RequestParam(name = "fileName", required = false) String fileName) {
        fileName = fileName == null? "IMDb_Movie_Database.csv" : fileName;
        return fileHolder.getFile(fileName);
    }

}
