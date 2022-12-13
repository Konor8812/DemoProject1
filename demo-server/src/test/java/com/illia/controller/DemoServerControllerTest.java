package com.illia.controller;


import com.illia.server.controller.DemoServerController;
import com.illia.server.request_processor.RequestProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = DemoServerController.class)
//@WebMvcTest
public class DemoServerControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RequestProcessor requestProcessor;

    MockMultipartFile mockedMultipartFile;

    @Test
    public void uploadMultipartFileTest() throws Exception {
        mvc.perform(multipart("/demo/uploadMultipartFile?fileName=f1")
                        .file(mockedMultipartFile))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Saved file on server")));
        verify(requestProcessor, atLeast(1)).proceedSaveFile("f1", mockedMultipartFile);

    }

    @Test
    public void downloadFileTestShouldBeOk() throws Exception {
        mvc.perform(get("/demo/downloadFile?fileName=f1"))
                .andExpect(status().isOk());
        verify(requestProcessor, atLeast(1)).proceedDownloadFile("f1");

    }

    @Test
    public void savedFileAmountTest() throws Exception {
        mvc.perform(get("/demo/savedFilesAmount"))
                .andExpect(status().isOk());
        verify(requestProcessor, atLeast(1)).getFilesAmount();
    }

    @BeforeEach
    public void prepareMocks(){
        mockedMultipartFile = new MockMultipartFile("file",
                "",
                "text/plain",
                "Content".getBytes());
        when(requestProcessor.proceedSaveFile(any(), (MultipartFile) any()))
                .thenReturn(ResponseEntity.ok().body("Saved file on server"));
        when(requestProcessor.proceedDownloadFile("f1"))
                .thenReturn(ResponseEntity.ok().build());
    }
}
