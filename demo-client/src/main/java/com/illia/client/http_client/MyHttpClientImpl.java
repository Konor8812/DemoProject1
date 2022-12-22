package com.illia.client.http_client;


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
import java.io.File;


@Slf4j
@Component
public class MyHttpClientImpl implements MyHttpClient {

    @Autowired
    RestTemplate restTemplate;


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
    public ResponseEntity<byte[]> performDownloadFileRequest(String url) {
        return restTemplate.getForEntity(url, byte[].class);
    }

}
