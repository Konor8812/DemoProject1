package com.illia.client.http;


import com.illia.client.config.ClientConfig;
import com.illia.client.http.MyHttpClient;
import com.illia.client.http.MyHttpClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = MyHttpClientImpl.class)
public class HttpClientTest {

    @MockBean
    RestTemplate restTemplate;

    @MockBean
    ClientConfig clientConfig;
    @Autowired
    MyHttpClient client;

    @Captor
    ArgumentCaptor<HttpEntity<MultiValueMap<String, Object>>> httpEntityArgumentCaptor;

    @Test
    public void uploadFileShouldInvokePostForEntity(){
        var fileName = "fileName";
        var expectedUrl = String.format(clientConfig.getBaseUrl(), "/uploadFile?fileName=", fileName);
        var overwriteFlag = true;
        var mockedResource = mock(ByteArrayResource.class);
        client.performUploadFileRequest("fileName", mockedResource, overwriteFlag);
        verify(restTemplate, times(1))
                .postForEntity(eq(expectedUrl), httpEntityArgumentCaptor.capture(), eq(String.class));

        var capturedEntity = httpEntityArgumentCaptor.getValue();
        var capturedResource = Objects.requireNonNull(capturedEntity.getBody()).getFirst("resource");
        assertEquals(mockedResource, capturedResource);

        var capturedHeaders = capturedEntity.getHeaders();
        var capturedOverwriteFlag = Boolean.parseBoolean(Objects.requireNonNull(capturedHeaders.get("overwrite")).get(0));
        assertTrue(capturedOverwriteFlag);
    }

    @Test
    public void downloadFileShouldInvokeGetForEntity() {
        var fileName = "fileName";
        var expectedUrl = String.format(clientConfig.getBaseUrl(), "/downloadFile?fileName=", fileName);
        client.performDownloadFileRequest(fileName);
        verify(restTemplate, times(1))
                .getForEntity(eq(expectedUrl), eq(byte[].class));
    }

    @Test
    public void downloadFileShouldBeBadRequest(){
        when(restTemplate.getForEntity("url/downloadFile?fileName=nonExistingFile", byte[].class))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "No such file!"));

        var exception = assertThrowsExactly(HttpClientErrorException.class,
                () -> client.performDownloadFileRequest("nonExistingFile"));
        assertTrue(exception.getStatusCode().is4xxClientError());
        assertTrue(exception.getMessage().contains("No such file!"));
    }

    @BeforeEach
    public void prepareMock(){
        when(clientConfig.getBaseUrl()).thenReturn("url%s%s");
    }

}
