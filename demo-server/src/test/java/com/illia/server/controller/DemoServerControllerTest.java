package com.illia.server.controller;


import com.illia.server.controller.DemoServerController;
import com.illia.server.request_processor.RequestProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = DemoServerController.class)
public class DemoServerControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RequestProcessor requestProcessor;

    @Test
    public void uploadFileTest() throws Exception {
        when(requestProcessor.proceedSaveFile(any(), any(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().body("Saved file on server"));

        mvc.perform(multipart("/demo/uploadFile?fileName=f1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Saved file on server")));
        verify(requestProcessor, times(1)).proceedSaveFile(any(), any(), anyBoolean());
    }

    @Test
    public void downloadFileTestShouldBeOk() throws Exception {
        when(requestProcessor.proceedDownloadFile("existingFile"))
                .thenReturn(ResponseEntity.ok().build());
        mvc.perform(get("/demo/downloadFile?fileName=existingFile"))
                .andExpect(status().isOk());
        verify(requestProcessor, times(1)).proceedDownloadFile("existingFile");
    }

    @Test
    public void savedFileAmountTest() throws Exception {
        mvc.perform(get("/demo/savedFilesAmount"))
                .andExpect(status().isOk());
        verify(requestProcessor, times(1)).getFilesAmount();
    }

}
