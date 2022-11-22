package com.illia.client.service;

import com.illia.client.http_client.HttpClientException;
import com.illia.client.http_client.MyHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;


@Slf4j
@Service
public class DemoService {

    @Autowired
    MyHttpClient client;

    public String uploadFile(String fileName, File file) {
        try {
            if (file == null) {
                file = new File(fileName);
            }
            client.uploadFile(fileName, file);
            return "Successfully uploaded " + fileName;
        } catch (HttpClientException e) {
            return e.getMessage() + fileName;
        }
    }

    public File downloadFile(String fileName) {
        try {
            return client.downloadFile(fileName);
        } catch (HttpClientException e) {
            return null;
        }
    }

}
