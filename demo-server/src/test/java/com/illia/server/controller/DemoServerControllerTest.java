package com.illia.server.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.illia.server.file.model.FileEntity.FileDocument;
import com.illia.server.request.RequestProcessor;
import com.illia.server.request.RequestProcessorException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(classes = DemoServerController.class)
public class DemoServerControllerTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  RequestProcessor requestProcessor;

  @Test
  public void uploadFileTest() throws Exception {
    when(requestProcessor.proceedSaveFile(eq("f1"), any(), anyBoolean()))
        .thenReturn("Saved file on server");

    mvc.perform(multipart("/demo/uploadFile?fileName=f1"))
        .andExpect(status().isOk())
        .andExpect(content().string(Matchers.equalTo("Saved file on server")));
    verify(requestProcessor, times(1)).proceedSaveFile(eq("f1"), any(), anyBoolean());
  }

  @Test
  public void uploadFileShouldHandleException() throws Exception {
    when(requestProcessor.proceedSaveFile(any(), any(), anyBoolean()))
        .thenThrow(new RequestProcessorException("rpe"));

    mvc.perform(multipart("/demo/uploadFile?fileName=f1"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Matchers.equalTo("rpe")));
    verify(requestProcessor, times(1)).proceedSaveFile(any(), any(), anyBoolean());
  }

  @Test
  public void downloadFileTestShouldBeOk() throws Exception {
    when(requestProcessor.proceedDownloadFile("existingFile"))
        .thenReturn(mock(FileDocument.class));
    mvc.perform(get("/demo/downloadFile?fileName=existingFile"))
        .andExpect(status().isOk());
    verify(requestProcessor, times(1)).proceedDownloadFile("existingFile");
  }

  @Test
  public void downloadFileShouldHandleException() throws Exception {
    when(requestProcessor.proceedDownloadFile(any()))
        .thenThrow(new RequestProcessorException("rpe"));
    mvc.perform(get("/demo/downloadFile?fileName="))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Matchers.equalTo("rpe")));
    verify(requestProcessor, times(1)).proceedDownloadFile(any());
  }

   @Test
  public void savedFileAmountTestShouldInvokeRequestProcessorMethod() throws Exception {
    mvc.perform(get("/demo/count"))
        .andExpect(status().isOk());
    verify(requestProcessor, times(1))
        .getFilesAmount();
  }

  @Test
  public void getAllFilesAmountShouldInvokeRequestProcessorMethod() throws Exception {
    mvc.perform(get("/demo/all"))
        .andExpect(status().isOk());
    verify(requestProcessor, times(1))
        .getAllSavedFiles();
  }
}
