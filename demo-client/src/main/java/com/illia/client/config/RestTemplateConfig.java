package com.illia.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class RestTemplateConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Autowired
//    @Bean
//    RestTemplate restTemplate(RestTemplateResponseErrorHandler restTemplateResponseErrorHandler) {
//        return new RestTemplateBuilder().errorHandler(restTemplateResponseErrorHandler).build();
//    }
//
//    @Component
//    public static class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
//
//        @Override
//        public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
//            return (httpResponse.getStatusCode().is4xxClientError()
//                    || httpResponse.getStatusCode().is5xxServerError());
//        }
//
//        @Override
//        public void handleError(ClientHttpResponse httpResponse) throws IOException {
//            var responseCode = httpResponse.getStatusCode();
//            System.out.println("Response code in handleError " + responseCode.value());
////            if (responseCode.is4xxClientError()){
////            }
//        }
//    }

}
