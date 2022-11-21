package com.illia.client.service;

import com.illia.client.http_client.MyHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DemoService {

    @Autowired
    MyHttpClient client;

    public boolean uploadFile(String fileName, File file){
        try {
            client.uploadFile(fileName, file);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public File downloadFile(String fileName){
        return client.downloadFile(fileName);
    }


    public long getRowsAmount(File file) throws IOException {
        try(var readerStream = new BufferedReader(new FileReader(file)).lines()){
            return readerStream.count();
        }
    }
}
