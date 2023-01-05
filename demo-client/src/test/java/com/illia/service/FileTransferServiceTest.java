package com.illia.service;

import com.illia.client.config.ClientConfig;
import com.illia.client.http_client.MyHttpClient;
import com.illia.client.service.FileTransferService;
import com.illia.client.service.FileHandlingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpClientErrorException;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {FileTransferService.class})
public class FileTransferServiceTest {

    @Autowired
     FileTransferService service;

    @MockBean
    MyHttpClient client;

    @MockBean
    FileHandlingService fileHandlingService;


    @Test
    public void uploadFileShouldBeOk() throws IOException {
        when(client.performUploadFileRequest(any(), any(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());
        when(fileHandlingService.resolveMultipartFile(notNull()))
                .thenReturn(mock(ByteArrayResource.class));

        var fileName = "fileName";
        var mockMultipartFile = new MockMultipartFile("fileName", "Content".getBytes());

        var response = service.uploadFile(fileName, mockMultipartFile, true);
        verify(client, times(1)).performUploadFileRequest(any(), any(), anyBoolean());
        verify(fileHandlingService, times(1)).resolveMultipartFile(mockMultipartFile);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void uploadFileShouldNotOverwrite() throws IOException {
        var fileName = "alreadyExistingFile";

        var mockMultipartFile = new MockMultipartFile(fileName, "Content".getBytes());
        var byteArrayResource = new ByteArrayResource("Content".getBytes());

        when(fileHandlingService.resolveMultipartFile(mockMultipartFile))
                .thenReturn(byteArrayResource);

        var mockException = mock(HttpClientErrorException.class);
        when(mockException.getResponseBodyAsString())
                .thenReturn("File already exists, no overwrite instructions");
        when(mockException.getStatusCode())
                .thenReturn(HttpStatus.BAD_REQUEST);

        when(client.performUploadFileRequest(fileName, byteArrayResource, false))
                .thenThrow(mockException);

        var response = service.uploadFile(fileName, mockMultipartFile, false);
        verify(client, times(1))
                .performUploadFileRequest(fileName, byteArrayResource, false);
        verify(fileHandlingService, times(1))
                .resolveMultipartFile(mockMultipartFile);
        assertTrue(response.getStatusCode().is4xxClientError());
    }
    @Test
    public void uploadFileShouldBeBadRequest() throws IOException {
        var response = service.uploadFile("fileName", null, true);
        verify(client, never()).performUploadFileRequest(any(), any(), anyBoolean());
        verify(fileHandlingService, never()).resolveMultipartFile(any());
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void downloadFileShouldBeOk() throws IOException {
        var fileName = "existingFile";
        var contentAsBytes = "Content".getBytes(StandardCharsets.UTF_8);

        when(fileHandlingService.saveFile(fileName, contentAsBytes, true))
                .thenReturn(true);
        when(client.performDownloadFileRequest(any()))
                .thenReturn(ResponseEntity.ok().body(contentAsBytes));

        var response = service.downloadFile(fileName, true);

        assertEquals(String.format("File %s saved successfully", fileName), response.getBody());

        verify(client, times(1))
                .performDownloadFileRequest(any());
        verify(fileHandlingService, times(1))
                .saveFile("existingFile", contentAsBytes, true);
    }

    @Test
    public void downloadFileShouldBeBadRequest() {
        var fileName = "nonExistingFile";
        when(client.performDownloadFileRequest(fileName))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        var response = service.downloadFile(fileName, true);
        verify(client, times(1)).performDownloadFileRequest(fileName);
        assertTrue(response.getStatusCode().is4xxClientError());
    }
}
