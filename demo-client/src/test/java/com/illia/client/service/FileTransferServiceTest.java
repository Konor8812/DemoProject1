package com.illia.client.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.illia.client.http.MyHttpClient;
import com.illia.client.model.file.FileEntity;
import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.file.FileHandlingService;
import com.illia.client.service.file.FileTransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(classes = {FileTransferService.class})
public class FileTransferServiceTest {

  @Autowired
  FileTransferService fileTransferService;

  @MockBean
  MyHttpClient client;

  @MockBean
  FileHandlingService fileHandlingService;

  @Test
  public void uploadFileShouldResolveResourceAndCallClient() throws FileHandlingException {
    var fileName = "fileName";
    var mockMultipartFile = mock(MultipartFile.class);
    var mockResource = mock(ByteArrayResource.class);

    when(fileHandlingService.resolveMultipartFile(mockMultipartFile))
        .thenReturn(mockResource);
    when(client.performUploadFileRequest(eq(fileName), eq(mockResource), anyBoolean()))
        .thenReturn(ResponseEntity.ok().build());

    assertTrue(fileTransferService.uploadFile(fileName, mockMultipartFile, true).getStatusCode().is2xxSuccessful());

    verify(fileHandlingService, times(1))
        .resolveMultipartFile(mockMultipartFile);
    verify(client, times(1))
        .performUploadFileRequest(eq(fileName), eq(mockResource), eq(true));
  }

  @Test
  public void downloadExistingFileShouldThrowException() {
    when(fileHandlingService.exists(any()))
        .thenReturn(true);
    var resp = assertThrowsExactly(FileHandlingException.class,
        () -> fileTransferService.downloadFile(null, false));
    verify(fileHandlingService, times(1)).exists(any());
    verifyNoInteractions(client);
    assertEquals("File with such name already exists!", resp.getMessage());
  }

  @Test
  public void downloadFileShouldCallClientAndSaveResponse() throws FileHandlingException {
    var fileName = "fileName";
    var preparedFileEntity = FileEntity.builder()
        .name(fileName)
        .content("content".getBytes())
        .build();
    ResponseEntity<FileEntity> preparedResponse = ResponseEntity.ok().body(preparedFileEntity);
    when(client.performDownloadFileRequest(any()))
        .thenReturn(preparedResponse);
    when(fileHandlingService.saveFile(eq(preparedFileEntity), eq(true)))
        .thenReturn("ok");
    var resp = fileTransferService.downloadFile(fileName, true);

    verify(client, times(1)).performDownloadFileRequest(any());
    assertEquals("ok", resp);
  }


}
