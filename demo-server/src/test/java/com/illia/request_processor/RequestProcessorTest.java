package com.illia.request_processor;

import com.illia.server.file_holder.FileHolder;
import com.illia.server.request_processor.RequestProcessor;
import com.illia.server.request_processor.RequestProcessorImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {RequestProcessorImpl.class})
public class RequestProcessorTest {

    @MockBean
    FileHolder fileholder;
    @Autowired
    RequestProcessor requestProcessor;

    @Test
    public void proceedDownloadFileRequestTestShouldReturnBadRequest(){
        var mockedFile = mock(File.class);
        when(fileholder.getFile("anyExistingFile")).thenReturn(mockedFile);

        var response = requestProcessor.proceedDownloadFile("notExistingFile");
        verify(fileholder, atLeast(1)).getFile(any());
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void proceedDownloadFileTestShouldReturnNotNull(){
        var mockedFile = mock(File.class);
        when(fileholder.getFile("anyExistingFile")).thenReturn(mockedFile);

        var response = requestProcessor.proceedDownloadFile("anyExistingFile");
        verify(fileholder, atLeast(1)).getFile(any());
        assertNotNull(response.getBody());
    }

    @Test
    public void uploadMultipartFileRequest(){
        var mockedMultipartFile = mock(MockMultipartFile.class);
        requestProcessor.proceedSaveFile("multipartFile", mockedMultipartFile);
        verify(fileholder, atLeast(1)).saveFile(any(), (MultipartFile) any());
    }

    @Test
    public void uploadFileRequest() {
        var mockedFile = mock(File.class);
        requestProcessor.proceedSaveFile("file", mockedFile);
        verify(fileholder, atLeast(1)).saveFile(any(), (File) any());
    }
}
