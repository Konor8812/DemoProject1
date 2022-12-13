package com.illia.service;

import com.illia.client.http_client.MyHttpClient;
import com.illia.client.service.DemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = DemoService.class)
public class DemoServiceTest {

    @MockBean
    MyHttpClient client;

    @Autowired
    DemoService service;

    @Test
    public void uploadFileShouldBeOk() {
        var response = service.uploadFile("Some file", mock(MultipartFile.class));
        verify(client, atLeast(1)).uploadFile(any(), any());
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void downloadFileShouldBeOk() {
        var fileName = "existingFile";
        var response = service.downloadFile(fileName);
        verify(client, atLeast(1)).downloadFile(fileName);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody() instanceof File);
    }

    @Test
    public void downloadFileShouldBeBadRequest() {
        var fileName = "nonExistingFile";
        var response = service.downloadFile(fileName);
        verify(client, atLeast(1)).downloadFile(fileName);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @BeforeEach
    public void prepareMocks(){
        when(client.uploadFile(any(), any())).thenReturn(ResponseEntity.ok().build());
        when(client.downloadFile("existingFile")).thenReturn(ResponseEntity.ok().body(mock(File.class)));
        when(client.downloadFile("nonExistingFile")).thenReturn(ResponseEntity.badRequest().build());
    }
}
