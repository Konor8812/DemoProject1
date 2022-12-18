package com.illia.client.service;

import com.illia.client.config.ClientConfig;
import com.illia.client.http_client.MyHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
@Service
public class DemoService {

    @Autowired
    ClientConfig clientConfig;

    @Autowired
    MyHttpClient client;

    public ResponseEntity<String> uploadFile(String fileName, MultipartFile multipartFile) {
        var url = String.format(clientConfig.getBaseUrl(), SupportedClientOperation.UPLOAD_FILE, fileName);

        File file = null;
        if (multipartFile != null) {
            file = new File(fileName);
            try (var is = multipartFile.getInputStream();
                 var os = new FileOutputStream(file)) {
                is.transferTo(os);
            } catch (IOException e) {
                var errorMsg = "Error during multipart file resolving!";
                log.error(errorMsg, e);
                return ResponseEntity.internalServerError().body(errorMsg);
            }
        }else {
            return ResponseEntity.badRequest().body("No file attached!");
        }
        return client.performUploadFileRequest(url, file);
    }

    public ResponseEntity<Object> downloadFile(String fileName) {
        var url = String.format(clientConfig.getBaseUrl(), SupportedClientOperation.DOWNLOAD_FILE, fileName);
        try {
            var response = client.performDownloadFileRequest(url);
            System.out.println(response);
            var body = (String) response.getBody();
            var savedFileName = saveFile(Path.of(body));
            if (savedFileName != null) {
                return ResponseEntity.ok().body("Successfully downloaded, file location - " + savedFileName);
            } else {
                return ResponseEntity.badRequest().body("Unable to save file! Reason: file exists no more ");
            }
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

    private static class SupportedClientOperation {
        private static final String UPLOAD_FILE = "/uploadFile?fileName=";
        private static final String DOWNLOAD_FILE = "/downloadFile?fileName=";
    }

    private Path saveFile(Path path) {
        var prefix = clientConfig.getDownloadedFilesDirectoryPrefix();
        var fileName = path.getFileName().toString();
        var currentFilePath = Path.of(prefix, fileName);
        for (int i = 1; ; i++) {
            if (!currentFilePath.toFile().exists()) {
                try {
                    return Files.copy(path, currentFilePath);
                } catch (IOException ex) {
                    log.error("Error trying to save file!", ex);
                    return null;
                }
            } else {
                var nextPath = String.format("%s\\%s(%d)", prefix, fileName, i);
                currentFilePath = Path.of(nextPath);
            }
        }

    }

}
