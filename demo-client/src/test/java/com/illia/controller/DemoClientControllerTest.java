package com.illia.controller;

import com.illia.client.controller.DemoClientController;
import com.illia.client.service.FileTransferService;

import com.illia.client.service.QueryProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = DemoClientController.class)
class DemoClientControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    FileTransferService fileTransferService;


    @MockBean
    QueryProcessingService queryProcessingService;

    @Test
    public void uploadFileShouldBeOk() throws Exception {
        when(fileTransferService.uploadFile(any(), any(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().body("Upload request sent"));

        var mockMultipartFile = new MockMultipartFile("multipartFile", "Content".getBytes());

        mvc.perform(multipart("/demo/uploadFile?fileName=fileName")
                        .file(mockMultipartFile))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Upload request sent")));
        verify(fileTransferService, times(1)).uploadFile("fileName", mockMultipartFile, false);
    }

    @Test
    public void downloadFileTestShouldBeOk() throws Exception {
        when(fileTransferService.downloadFile("fileName", false))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/demo/downloadFile?fileName=fileName"))
                .andExpect(status().isOk());
        verify(fileTransferService, times(1)).downloadFile("fileName", false);

    }

}
