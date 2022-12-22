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
import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {RequestProcessorImpl.class})
public class RequestProcessorTest {

    @MockBean
    FileHolder fileholder;
    @Autowired
    RequestProcessor requestProcessor;

    @Test
    public void proceedDownloadFileRequestTestShouldReturnBadRequest() throws IOException {
        when(fileholder.getFile("existingFile"))
                .thenReturn("Content".getBytes());

        var response = requestProcessor.proceedDownloadFile("notExistingFile");
        verify(fileholder, atLeast(1)).getFile("notExistingFile");
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void proceedDownloadFileTestShouldReturnNotNull() throws IOException {
        when(fileholder.getFile("existingFile"))
                .thenReturn("Content".getBytes());

        var response = requestProcessor.proceedDownloadFile("existingFile");
        verify(fileholder, atLeast(1)).getFile("existingFile");
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
    }

    @Test
    public void proceedUploadFileRequestShouldBeOk() {
        var fileName = "mockFile";
        var mockedFile = mock(File.class);
        when(fileholder.saveFile(fileName, mockedFile))
                .thenReturn("Saved file on server mockFile");

        var response = requestProcessor.proceedSaveFile(fileName, mockedFile);
        var body = response.getBody();
        assertEquals("Saved file on server mockFile", body);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(fileholder, atLeast(1)).saveFile(any(), any());
    }

    @Test
    public void getFilesAmountTest(){
        var randomInt = new Random().nextInt();
        when(fileholder.getFilesAmount())
                .thenReturn(randomInt);

        var response = requestProcessor.getFilesAmount();
        if(randomInt >= 0 ){
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(randomInt, response.getBody());
        }else{
            assertTrue(response.getStatusCode().is5xxServerError());
        }

    }

}
