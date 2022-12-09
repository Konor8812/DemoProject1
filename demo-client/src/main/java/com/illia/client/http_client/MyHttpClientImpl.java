package com.illia.client.http_client;


import com.illia.client.config.ClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;


@Slf4j
@Component
public class MyHttpClientImpl implements MyHttpClient {

    @Autowired
    ClientConfig clientConfig;

    @Override
    public ResponseEntity<String> uploadFile(String fileName, MultipartFile mfile) {
        var operation = SupportedClientOperation.UPLOAD_FILE;
        var url = String.format(clientConfig.getBaseUrl(), operation, fileName);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        var file = new File(fileName);
        try (var is = mfile.getInputStream();
             var os = new FileOutputStream(file)) {
            is.transferTo(os);
            body.add("file", file);
        } catch (IOException e) {
            var errorMsg = "Error during multipart file resolving!";
            log.error(errorMsg);
            return ResponseEntity.internalServerError().body(errorMsg);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(url, requestEntity, String.class);
    }

    @Override
    public ResponseEntity<File> downloadFile(String fileName) {
        var operation = SupportedClientOperation.DOWNLOAD_FILE;
        var url = String.format(clientConfig.getBaseUrl(), operation, fileName);

        RestTemplate restTemplate = new RestTemplate();
        var response = restTemplate.getForEntity(url, File.class);
        var responseCode = response.getStatusCode();

        if(responseCode.is4xxClientError()){

        }else if (responseCode.is5xxServerError()){

        } else {
        }
        return response;

    }

    private static class SupportedClientOperation {
        private static final String UPLOAD_FILE = "/uploadFile?fileName=";
        private static final String DOWNLOAD_FILE = "/downloadFile?fileName=";
    }

}
