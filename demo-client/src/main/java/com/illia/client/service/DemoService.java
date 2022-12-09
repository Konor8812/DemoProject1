package com.illia.client.service;

import com.illia.client.http_client.MyHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@Slf4j
@Service
public class DemoService {

    @Autowired
    MyHttpClient client;

    public ResponseEntity<String> uploadFile(String fileName, MultipartFile file) {
        return client.uploadFile(fileName, file);
    }

    public ResponseEntity<File> downloadFile(String fileName) {
        return client.downloadFile(fileName);
    }
}
