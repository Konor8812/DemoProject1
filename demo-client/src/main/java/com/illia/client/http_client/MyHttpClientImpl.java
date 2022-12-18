package com.illia.client.http_client;


import com.illia.client.config.ClientConfig;
import com.illia.client.model.IMDbMovieReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
@Component
public class MyHttpClientImpl implements MyHttpClient {

    @Autowired
    RestTemplate restTemplate;

    //    @Override
//    public ResponseEntity<String> uploadFile(String fileName, MultipartFile mfile) {
//        var operation = SupportedClientOperation.UPLOAD_FILE;
//        var url = String.format(clientConfig.getBaseUrl(), operation, fileName);
//
//        var headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//
//        if(mfile != null) {
//            var file = new File(fileName);
//            try (var is = mfile.getInputStream();
//                 var os = new FileOutputStream(file)) {
//                is.transferTo(os);
//                body.add("file", file);
//            } catch (IOException e) {
//                var errorMsg = "Error during multipart file resolving!";
//                log.error(errorMsg);
//                return ResponseEntity.internalServerError().body(errorMsg);
//            }
//        }else {
//            body.add("file", null);
//        }
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//        return restTemplate.postForEntity(url, requestEntity, String.class);
//    }

//    @Override
//    public ResponseEntity<Object> downloadFile(String fileName) {
//        var operation = SupportedClientOperation.DOWNLOAD_FILE;
//        var url = String.format(clientConfig.getBaseUrl(), operation, fileName);
//
//        try {
//            var response = restTemplate.getForEntity(url, Object.class);
//            var body = (String) response.getBody();
//
//            var savedFileName = saveFile(Path.of(body));
//            if (savedFileName != null) {
//                return ResponseEntity.ok().body("Successfully downloaded, file location - " + savedFileName);
//            } else {
//                return ResponseEntity.badRequest().body("Unable to save file! Reason: file exists no more ");
//            }
//
//        } catch (HttpClientErrorException ex) {
//            var statusCode = ex.getStatusCode();
//            var serverResponse = ex.getResponseBodyAsString();
//            if (statusCode.is4xxClientError()) {
//                return ResponseEntity.badRequest().body(serverResponse);
//            } else {
//                return ResponseEntity.internalServerError().body(serverResponse);
//            }
//        }
//    }

    @Override
    public ResponseEntity<String> performUploadFileRequest(String url, File file) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(url, requestEntity, String.class);
    }

    @Override
    public ResponseEntity<Object> performDownloadFileRequest(String url) throws HttpClientErrorException{
        return restTemplate.getForEntity(url, Object.class);
    }

}
