package com.illia.client.service.file;

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

    // this receives ResponseEntity from restTemplate.postForEntity
    // it sounds reasonable to just return without modifications
    public ResponseEntity<String> uploadFile(String fileName, MultipartFile multipartFile, boolean overwrite) throws FileHandlingException {
        try {
            var bytes = fileHandlingService.resolveMultipartFile(multipartFile);

            return client.performUploadFileRequest(fileName, bytes, overwrite);
        } catch (IOException e) {
            var errorMsg = "Inner error while file resolving!";
            log.error(errorMsg, e);
            throw new FileHandlingError("Internal error while saving file!");
        } catch (HttpClientErrorException ex) {
            var statusCode = ex.getStatusCode();
            var serverResponse = ex.getResponseBodyAsString();
            if (statusCode.is4xxClientError()) {
                throw new FileHandlingException(serverResponse);
            } else {
                throw new FileHandlingError(serverResponse);
            }
        }
    }

    public String downloadFile(String fileName, boolean overwrite) throws FileHandlingException {
        if (!overwrite) {
            if(fileHandlingService.exists(fileName)) {
                throw new FileHandlingException("File with such name exists!");
            }
        }

        try {
            var resp = client.performDownloadFileRequest(fileName);
            var content = resp.getBody();
            var saved = fileHandlingService.saveFile(fileName, content, true);
            if(saved) {
                return String.format("File %s saved successfully", fileName);
            }else {
                throw new FileHandlingError("Internal error while saving file!");
            }

        } catch (HttpClientErrorException ex) {
            var statusCode = ex.getStatusCode();
            var serverResponse = ex.getResponseBodyAsString();
            if (statusCode.is4xxClientError()) {
                return serverResponse;
            } else {
                throw new FileHandlingError(serverResponse);
            }
        } catch (IOException e) {
            log.error("Error in save file", e);
            throw new FileHandlingError("Internal error while saving file!");
        }
    }

    public String deleteFile(String fileName) {
        try {
            var deleted = fileHandlingService.deleteFile(fileName);
            if (deleted) {
                return "Successfully deleted " + fileName;
            } else {
                throw new FileHandlingException("No such file");
            }
        } catch (Exception e) {
            var errorMsg = "Inner error while file deleting!";
            log.error(errorMsg, e);
            throw new FileHandlingError(errorMsg);
        }
    }

}
