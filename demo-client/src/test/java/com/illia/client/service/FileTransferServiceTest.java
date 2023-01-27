package com.illia.client.service;

import com.illia.client.http.MyHttpClient;
import com.illia.client.service.file.FileHandlingError;
import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.file.FileTransferService;
import com.illia.client.service.file.FileHandlingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {FileTransferService.class})
public class FileTransferServiceTest {

    @Autowired
    FileTransferService fileTransferService;

    @MockBean
    MyHttpClient client;

    @MockBean
    FileHandlingService fileHandlingService;

    @Test
    public void uploadFileShouldResolveResourceAndCallClient() throws IOException, FileHandlingException {
        var fileName = "fileName";
        when(client.performUploadFileRequest(eq(fileName), any(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        var response = fileTransferService.uploadFile(fileName, null, true);

        verify(client, times(1))
                .performUploadFileRequest(eq(fileName), any(), eq(true));
        verify(fileHandlingService, times(1))
                .resolveMultipartFile(any());
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void uploadFileShouldThrowFileHandlingException() throws IOException {
        var exceptionMsg = "File already exists";
        when(client.performUploadFileRequest(any(), any(), anyBoolean()))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", exceptionMsg.getBytes(), StandardCharsets.ISO_8859_1));

        var resp = assertThrowsExactly(FileHandlingException.class,
                () -> fileTransferService.uploadFile(null, null, false));

        verify(fileHandlingService, times(1))
                .resolveMultipartFile(any());
        verify(client, times(1))
                .performUploadFileRequest(any(), any(), anyBoolean());
        assertEquals(exceptionMsg, resp.getMessage());
    }

    @Test
    public void uploadFileShouldThrowFileHandlingError() throws IOException {
        when(fileHandlingService.resolveMultipartFile(any()))
                .thenThrow(new IOException());

        var resp = assertThrowsExactly(FileHandlingError.class,
                () -> fileTransferService.uploadFile(null, null, false));

        verify(fileHandlingService, times(1))
                .resolveMultipartFile(any());
        assertEquals("Internal error while saving file!", resp.getMessage());
        verifyNoInteractions(client);
    }

    @Test
    public void downloadExistingFileShouldThrowException() {
        when(fileHandlingService.exists(any()))
                .thenReturn(true);
        var resp = assertThrowsExactly(FileHandlingException.class, () -> fileTransferService.downloadFile(null, false));
        verify(fileHandlingService, times(1)).exists(any());
        verifyNoInteractions(client);
        assertEquals("File with such name exists!", resp.getMessage());
    }

    @Test
    public void downloadFileServerErrorShouldThrowError() {
        var exceptionMsg = "server response message";
        when(client.performDownloadFileRequest(any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "", exceptionMsg.getBytes(), StandardCharsets.ISO_8859_1));
        var resp = assertThrowsExactly(FileHandlingError.class, () -> fileTransferService.downloadFile(null, true));

        verify(client, times(1)).performDownloadFileRequest(any());
        assertEquals(exceptionMsg, resp.getMessage());
    }

    @Test
    public void downloadFileServerErrorShouldFileHandlingThrowException() throws FileHandlingException {
        var exceptionMsg = "server response message";
        when(client.performDownloadFileRequest(any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", exceptionMsg.getBytes(), StandardCharsets.ISO_8859_1));
        var resp = fileTransferService.downloadFile(null, true);

        verify(client, times(1)).performDownloadFileRequest(any());
        assertEquals(exceptionMsg, resp);
    }

    @Test
    public void downloadFileShouldBeOk() throws IOException, FileHandlingException {
        var fileName = "fileName";
        var byteContent = "content".getBytes();
        when(client.performDownloadFileRequest(fileName))
                .thenReturn(ResponseEntity.ok(byteContent));
        when(fileHandlingService.saveFile(fileName, byteContent, true))
                .thenReturn(true);
        var resp = fileTransferService.downloadFile(fileName, true);
        assertEquals(String.format("File %s saved successfully", fileName), resp);
        verify(client, times(1))
                .performDownloadFileRequest(eq(fileName));
        verify(fileHandlingService, times(1))
                .saveFile(eq(fileName), eq(byteContent), eq(true));

    }

}
