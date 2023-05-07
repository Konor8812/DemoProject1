package com.illia.server.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illia.server.request.RequestProcessor;
import com.illia.server.request.RequestProcessorException;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(classes = {DemoServerController.class, ObjectMapper.class})
public class DemoServerControllerTest {

  @Autowired
  MockMvc mvc;
  @MockBean
  RequestProcessor requestProcessor;
  @Autowired
  ObjectMapper objectMapper;

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
    var fileName = "existingFile";
    mvc.perform(get("/demo/downloadFile?fileName=existingFile")
            .contentType("application/json"))
        .andExpect(status().isOk());
    verify(requestProcessor, times(1)).proceedDownloadFile(fileName);
  }

  @Test
  public void downloadFileShouldHandleException() throws Exception {
    when(requestProcessor.proceedDownloadFile(any()))
        .thenThrow(new RequestProcessorException("rpe"));
    mvc.perform(get("/demo/downloadFile?fileName=")
            .contentType("application/json"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Matchers.equalTo("rpe")));
    verify(requestProcessor, times(1)).proceedDownloadFile(any());
  }

  @Test
  public void savedFileAmountTestShouldInvokeRequestProcessorMethod() throws Exception {
    when(requestProcessor.getFilesAmount())
        .thenReturn(0L);
    mvc.perform(get("/demo/count")
            .contentType("application/json"))
        .andExpect(status().isOk());
    verify(requestProcessor, times(1))
        .getFilesAmount();
  }

  @Test
  public void getAllFilesAmountShouldInvokeRequestProcessorMethod() throws Exception {
    when(requestProcessor.getAllSavedFiles())
        .thenReturn(List.of());
    mvc.perform(get("/demo/all")
            .contentType("application/json"))
        .andExpect(status().isOk());
    verify(requestProcessor, times(1))
        .getAllSavedFiles();
  }
}
