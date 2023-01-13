package com.illia.client.service;

import com.illia.client.http.MyHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


@Slf4j
@Service
public class FileTransferService {

    @Autowired
    private MyHttpClient client;

    @Autowired
    private FileHandlingService fileHandlingService;


    public ResponseEntity<String> uploadFile(String fileName, MultipartFile multipartFile, boolean overwrite) {
        if (multipartFile == null) {
            return ResponseEntity.badRequest().body("No file attached!");
        }
        try {
            var bytes = fileHandlingService.resolveMultipartFile(multipartFile);

            return client.performUploadFileRequest(fileName, bytes, overwrite);
        } catch (IOException e) {
            var errorMsg = "Inner error while file resolving!";
            log.error(errorMsg, e);
            return ResponseEntity.internalServerError().body(errorMsg);
        } catch (HttpClientErrorException ex) {
            var statusCode = ex.getStatusCode();
            var serverResponse = ex.getResponseBodyAsString();
            if (statusCode.is4xxClientError()) {
                return ResponseEntity.badRequest().body(serverResponse);
            } else {
                return ResponseEntity.internalServerError().body(serverResponse);
            }
        }
    }

    public ResponseEntity<String> downloadFile(String fileName, boolean overwrite) {
        if (!overwrite) {
            if(fileHandlingService.exists(fileName)) {
                return ResponseEntity.badRequest().body("File with such name exists! " +
                        "Consider adding &overwrite=true to url to overwrite existing file");
            }
        }

        try {
            var resp = client.performDownloadFileRequest(fileName);
            var content = resp.getBody();
            var saved = fileHandlingService.saveFile(fileName, content, true);
            if(saved) {
                return ResponseEntity.ok().body(String.format("File %s saved successfully", fileName));
            }

            return ResponseEntity.internalServerError().body("Internal server error while saving file!");

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

    public ResponseEntity<String> deleteFile(String fileName) {
        try {
            var deleted = fileHandlingService.deleteFile(fileName);
            if (deleted) {
                return ResponseEntity.ok().body("Successfully deleted " + fileName);
            } else {
                return ResponseEntity.badRequest().body("No such file");
            }
        } catch (Exception e) {
            var errorMsg = "Inner error while file deleting!";
            log.error(errorMsg, e);
            return ResponseEntity.internalServerError().body(errorMsg);
        }
    }

}
