package com.illia.client.http_client;


import com.illia.client.config.ClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;

import java.io.File;


@Slf4j
@Component
public class MyHttpClientImpl implements MyHttpClient {

    @Autowired
    ClientConfig clientConfig;

    @Override
    public String uploadFile(String fileName, File file) {

        var operation = "/uploadFile";
        var params = String.format("?fileName=%s", fileName);
        var url = createUrl(clientConfig.getBaseUrl(), operation, params);

        try {
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            var response = restTemplate.postForEntity(url, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            var errorMsg = "Error during file uploading " + e.getMessage();
            log.error(errorMsg);
            throw new HttpClientException(errorMsg);
        }
    }

    @Override
    public File downloadFile(String fileName) {
        var operation = "/downloadFile";
        var params = String.format("?fileName=%s", fileName);
        var url = createUrl(clientConfig.getBaseUrl(), operation, params);

        RestTemplate restTemplate = new RestTemplate();

        var fileResponse = restTemplate.getForEntity(url, File.class);
        var statusCode = fileResponse.getStatusCode();

        var file = fileResponse.getBody();

        if(file == null){
            log.info("No such file {}", fileName);
            throw new HttpClientException();
        }
        log.info("Download file response code {}, fileName {}", statusCode.value(), file.getName());
        return file;
    }

    private String createUrl(String baseUrl, String operation, String params) {
        return String.format("%s%s%s", baseUrl, operation, params);
    }

}
