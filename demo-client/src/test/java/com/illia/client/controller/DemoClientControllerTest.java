package com.illia.client.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.file.FileHandlingService;
import com.illia.client.service.file.FileTransferService;
import com.illia.client.service.query.QueryProcessingService;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(classes = {DemoClientController.class})
class DemoClientControllerTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  FileTransferService fileTransferService;
  @MockBean
  FileHandlingService fileHandlingService;
  @MockBean
  QueryProcessingService queryProcessingService;


  @Test
  public void uploadValidFileShouldBeOk() throws Exception {
    var responseMsg = "Upload request sent";
    when(fileTransferService.uploadFile(any(), any(), anyBoolean()))
        .thenReturn(ResponseEntity.ok().body(responseMsg));

    var mockMultipartFile = new MockMultipartFile("multipartFile", "Content".getBytes());

    mvc.perform(multipart("/demo/uploadFile?fileName=fileName")
            .file(mockMultipartFile))
        .andExpect(status().isOk())
        .andExpect(result -> containsString(responseMsg));
    verify(fileTransferService, times(1)).uploadFile("fileName", mockMultipartFile, false);
  }

  @Test
  public void uploadNullFileShouldThrowException() throws Exception {
    var exceptionMsg = "No file attached";
    when(fileTransferService.uploadFile(any(), any(), anyBoolean()))
        .thenThrow(new FileHandlingException(exceptionMsg));

    var mockMultipartFile = new MockMultipartFile("multipartFile", "Content".getBytes());

    mvc.perform(multipart("/demo/uploadFile?fileName=fileName")
            .file(mockMultipartFile))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof FileHandlingException))
        .andExpect(result -> assertEquals(exceptionMsg, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    verify(fileTransferService, times(1)).uploadFile(eq("fileName"), eq(mockMultipartFile), anyBoolean());
  }

  @Test
  public void downloadExistingFileTestShouldBeOk() throws Exception {
    when(fileTransferService.downloadFile("fileName", false))
        .thenReturn("Success");

    mvc.perform(get("/demo/downloadFile?fileName=fileName"))
        .andExpect(status().isOk())
        .andExpect(result -> containsString("Success"));
    verify(fileTransferService, times(1)).downloadFile("fileName", false);

  }


  @Test
  public void downloadNonExistingFileShouldThrowException() throws Exception {
    var errorMsg = "No such file";
    when(fileTransferService.downloadFile("fileName", false))
        .thenThrow(new FileHandlingException(errorMsg));

    mvc.perform(get("/demo/downloadFile?fileName=fileName"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof FileHandlingException))
        .andExpect(result -> assertEquals(errorMsg, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    verify(fileTransferService, times(1)).downloadFile("fileName", false);
  }
}

