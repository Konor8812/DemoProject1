package com.illia.service;

import com.illia.client.http_client.MyHttpClient;
import com.illia.client.service.FileTransferService;
import com.illia.client.service.FileHandlingProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;


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
    FileHandlingProxyService fileHandlingProxyService;

    @Test
    public void uploadFileShouldResolveResourceAndCallClient() throws IOException {
        var fileName = "fileName";
        var mockMultipartFile = mock(MultipartFile.class);
        var mockResource = mock(ByteArrayResource.class);

        when(client.performUploadFileRequest(eq(fileName), eq(mockResource), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());
        when(fileHandlingProxyService.resolveMultipartFile(notNull()))
                .thenReturn(mockResource);

        var response = service.uploadFile(fileName, mockMultipartFile, true);

        verify(client, times(1))
                .performUploadFileRequest(eq(fileName), eq(mockResource), eq(true));
        verify(fileHandlingProxyService, times(1))
                .resolveMultipartFile(eq(mockMultipartFile));
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void uploadFileShouldBeBadRequestWithOverwriteFalse() throws IOException {
        var fileName = "alreadyExistingFile";

        var mockMultipartFile = mock(MultipartFile.class);
        var byteArrayResource = mock(ByteArrayResource.class);

        when(fileHandlingProxyService.resolveMultipartFile(notNull()))
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
        verify(fileHandlingProxyService, times(1))
                .resolveMultipartFile(mockMultipartFile);
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals("File already exists, no overwrite instructions", response.getBody());
    }
    @Test
    public void uploadFileWithMultipartFileNullShouldBeBadRequest() throws IOException {
        var response = service.uploadFile("fileName", null, true);
        verify(client, never()).performUploadFileRequest(any(), any(), anyBoolean());
        verify(fileHandlingProxyService, never()).resolveMultipartFile(any());
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals("No file attached!", response.getBody());
    }

    @Test
    public void downloadFileShouldCallClientAndFileHandlingProxyService() throws IOException {
        var fileName = "existingFile";
        var contentAsBytes = new byte[]{};

        when(client.performDownloadFileRequest(fileName))
                .thenReturn(ResponseEntity.ok().body(contentAsBytes));
        when(fileHandlingProxyService.saveFile(fileName, contentAsBytes, true))
                .thenReturn(true);

        var response = service.downloadFile(fileName, true);

        assertEquals(String.format("File %s saved successfully", fileName), response.getBody());

        verify(client, times(1))
                .performDownloadFileRequest(fileName);
        verify(fileHandlingProxyService, times(1))
                .saveFile("existingFile", contentAsBytes, true);
    }

    @Test
    public void downloadNonExistingFileShouldBeBadRequest() throws IOException {
        var fileName = "nonExistingFile";
        when(client.performDownloadFileRequest(fileName))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        var response = service.downloadFile(fileName, true);
        verify(client, times(1)).performDownloadFileRequest(fileName);
        verify(fileHandlingProxyService, never()).saveFile(eq(fileName), any(), eq(true));
        assertTrue(response.getStatusCode().is4xxClientError());
    }
}
