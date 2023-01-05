package com.illia.client;


import com.illia.client.config.ClientConfig;
import com.illia.client.http_client.MyHttpClient;
import com.illia.client.http_client.MyHttpClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

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


    @Test
    public void uploadFileShouldBeOk(){
        client.performUploadFileRequest("fileName", mock(ByteArrayResource.class), true);
        verify(restTemplate, times(1)).postForEntity((String) any(), any(), any());
    }

    @Test
    public void downloadFileShouldBeOk() {
        when(restTemplate.getForEntity("url/downloadFile?fileName=existingFile", byte[].class))
                .thenReturn(ResponseEntity.ok().body("filePath".getBytes(Charset.defaultCharset())));

        var response = client.performDownloadFileRequest("existingFile");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        var expectedMessage = "filePath";
        var actualMessage = new String(response.getBody());
        assertEquals(expectedMessage, actualMessage);
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
