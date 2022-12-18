package com.illia.client;


import com.illia.client.config.ClientConfig;
import com.illia.client.http_client.MyHttpClient;
import com.illia.client.http_client.MyHttpClientImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
        client.performUploadFileRequest("fileName", null);
        verify(restTemplate, atLeastOnce()).postForEntity((String) any(), any(), any());
    }

    @Test
    public void downloadFileShouldBeOk() {
        when(restTemplate.getForEntity("url/existingFile", Object.class)).thenReturn(ResponseEntity.ok().body("filePath"));
        var response = client.performDownloadFileRequest("url/existingFile");
        assertTrue(response.getStatusCode().is2xxSuccessful());

        var expectedMessage = "filePath";
        var actualMessage = (String) response.getBody();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void downloadFileShouldBeBadRequest(){
        when(restTemplate.getForEntity("url/nonExistingFile", Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "No such file!"));

        var exception = assertThrowsExactly(HttpClientErrorException.class,
                () -> client.performDownloadFileRequest("url/nonExistingFile"));
        assertTrue(exception.getStatusCode().is4xxClientError());
        assertTrue(exception.getMessage().contains("No such file!"));
    }

}
