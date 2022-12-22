package com.illia.client.service;

import com.illia.client.config.ClientConfig;
import com.illia.client.http_client.MyHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


@Slf4j
@Service
public class DemoService {

    @Autowired
    ClientConfig clientConfig;

    @Autowired
    MyHttpClient client;

    @Autowired
    FileHandlingService fileHandlingService;
    private static final String UPLOAD_FILE_BASE_URL = "/uploadFile?fileName=";
    private static final String DOWNLOAD_FILE_BASE_URL = "/downloadFile?fileName=";

    public ResponseEntity<String> uploadFile(String fileName, MultipartFile multipartFile) {
        var url = String.format(clientConfig.getBaseUrl(), UPLOAD_FILE_BASE_URL, fileName);

        try {
            File file = fileHandlingService.resolveMultipartFile(fileName, multipartFile);
            if(file == null){
                return ResponseEntity.badRequest().body("No file attached!");
            }
            return client.performUploadFileRequest(url, file);
        } catch (IOException e){
            var errorMsg = "Error during multipart file resolving!";
            log.error(errorMsg, e);
            return ResponseEntity.internalServerError().body(errorMsg);
        }

    }

    public ResponseEntity<String> downloadFile(String fileName) throws IOException {
        var url = String.format(clientConfig.getBaseUrl(), DOWNLOAD_FILE_BASE_URL, fileName);
        try {
            var resp = client.performDownloadFileRequest(url);
            var content = resp.getBody();

            var createdNewFile = fileHandlingService.saveFile(fileName, content);

            if (createdNewFile) {
                return ResponseEntity.ok().body("Created new file - " + fileName);
            } else {
                return ResponseEntity.ok().body("Updated existing file - " + fileName);
            }

        } catch (HttpClientErrorException ex) {
            var statusCode = ex.getStatusCode();
            var serverResponse = ex.getResponseBodyAsString();
            if (statusCode.is4xxClientError()) {
                return ResponseEntity.badRequest().body(serverResponse);
            } else {
                return ResponseEntity.internalServerError().body(serverResponse);
            }
        } catch (IOException e) {
            log.error("Error in save file", e);
            return ResponseEntity.internalServerError().body("Inner error while trying to save file");
        }

    }

}
