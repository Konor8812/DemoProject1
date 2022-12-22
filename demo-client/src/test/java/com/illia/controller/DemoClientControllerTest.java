package com.illia.controller;

import com.illia.client.controller.DemoClientController;
import com.illia.client.service.DemoService;

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
    DemoService service;

    MockMultipartFile mockedMultipartFile;

    @Test
    public void uploadFileShouldBeOk() throws Exception {
        when(service.uploadFile(any(), any()))
                .thenReturn(ResponseEntity.ok().body("Upload request sent"));

        mockedMultipartFile = new MockMultipartFile("file",
                "",
                "text/plain",
                "Content".getBytes());

        mvc.perform(multipart("/demo/uploadFile?fileName=fileName")
                        .file(mockedMultipartFile))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Upload request sent")));
        verify(service, atLeast(1)).uploadFile("fileName", mockedMultipartFile);
    }

    @Test
    public void downloadFileTestShouldBeOk() throws Exception {
        when(service.downloadFile("file"))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/demo/downloadFile?fileName=fileName"))
                .andExpect(status().isOk());
        verify(service, atLeast(1)).downloadFile("fileName");

    }

}
