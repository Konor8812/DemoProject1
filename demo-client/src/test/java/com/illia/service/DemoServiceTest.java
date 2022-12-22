package com.illia.service;

import com.illia.client.config.ClientConfig;
import com.illia.client.http_client.MyHttpClient;
import com.illia.client.service.DemoService;
import com.illia.client.service.FileHandlingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpClientErrorException;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {DemoService.class})
public class DemoServiceTest {

    @MockBean
    MyHttpClient client;
    @MockBean
    ClientConfig config;
    @MockBean
    FileHandlingService fileHandlingService;

    @Autowired
    DemoService service;

    @Test
    public void uploadFileShouldBeOk() throws IOException {
        when(client.performUploadFileRequest(any(), any()))
                .thenReturn(ResponseEntity.ok().build());

        when(fileHandlingService.resolveMultipartFile(any(), notNull()))
                .thenReturn(mock(File.class));

        var fileName = "fileName";
        var mockMultipartFile = new MockMultipartFile("fileName", "Content".getBytes());

        var response = service.uploadFile(fileName, mockMultipartFile);
        verify(client, atLeast(1)).performUploadFileRequest(any(), any());
        assertTrue(response.getStatusCode().is2xxSuccessful());

    }

    @Test
    public void uploadFileShouldBeBadRequest() throws IOException {
        when(client.performUploadFileRequest(any(), any()))
                .thenReturn(ResponseEntity.ok().build());
        when(fileHandlingService.resolveMultipartFile(any(), isNull()))
                .thenReturn(null);

        var response = service.uploadFile("fileName", null);
        verify(client, never()).performUploadFileRequest(any(), any());
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void downloadFileShouldBeOk() throws IOException {
        var fileName = "existingFile";
        var contentAsBytes = "Content".getBytes(StandardCharsets.UTF_8);

        when(client.performDownloadFileRequest("baseUrl/downloadFile?fileName=existingFile"))
                .thenReturn(ResponseEntity.ok().body(contentAsBytes));

        service.downloadFile(fileName);

        verify(client, atLeast(1))
                .performDownloadFileRequest("baseUrl/downloadFile?fileName=existingFile");
        verify(fileHandlingService, atLeast(1))
                .saveFile("existingFile", contentAsBytes);
    }

    @Test
    public void downloadFileShouldBeBadRequest() throws IOException {
        var fileName = "nonExistingFile";
        when(client.performDownloadFileRequest("baseUrl/downloadFile?fileName=nonExistingFile"))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        var response = service.downloadFile(fileName);
        verify(client, atLeast(1)).performDownloadFileRequest("baseUrl/downloadFile?fileName=nonExistingFile");
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @BeforeEach
    public void prepareMocks(){
        when(config.getBaseUrl()).thenReturn("baseUrl%s%s");
    }

}
