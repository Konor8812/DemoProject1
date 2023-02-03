package com.illia.client.http;


import com.illia.client.config.ClientConfig;
import com.illia.client.service.file.FileHandlingError;
import com.illia.client.service.file.FileHandlingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
public class MyHttpClientImpl implements MyHttpClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClientConfig clientConfig;

    private static final String UPLOAD_FILE_BASE_URL = "/uploadFile?fileName=";
    private static final String DOWNLOAD_FILE_BASE_URL = "/downloadFile?fileName=";

    @Override
    public ResponseEntity<String> performUploadFileRequest(String fileName, ByteArrayResource resource, boolean overwrite) throws FileHandlingException {
        var url = String.format(clientConfig.getBaseUrl(), UPLOAD_FILE_BASE_URL, fileName);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        if (overwrite){
            headers.add("Overwrite", "true");
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("resource", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try{
            return restTemplate.postForEntity(url, requestEntity, String.class);
        }catch (HttpClientErrorException ex){
            if (ex.getStatusCode().is4xxClientError()) {
                throw new FileHandlingException(ex.getResponseBodyAsString());
            } else {
                throw new FileHandlingError(ex.getResponseBodyAsString());
            }
        }
    }

    @Override
    public ResponseEntity<byte[]> performDownloadFileRequest(String fileName) throws FileHandlingException {
        var url = String.format(clientConfig.getBaseUrl(), DOWNLOAD_FILE_BASE_URL, fileName);
        try{
            return restTemplate.getForEntity(url, byte[].class);
        } catch (HttpClientErrorException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getResponseBodyAsString());
            if (ex.getStatusCode().is4xxClientError()) {
                throw new FileHandlingException(ex.getResponseBodyAsString());
            } else {
                throw new FileHandlingError(ex.getResponseBodyAsString());
            }
        }

    }

}
