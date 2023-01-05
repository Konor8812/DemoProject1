package com.illia.request_processor;

import com.illia.server.file_holder.FileHolder;
import com.illia.server.request_processor.RequestProcessor;
import com.illia.server.request_processor.RequestProcessorImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;

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
    public void proceedUploadFileRequestShouldBeOk() throws IOException {
        var fileName = "mockFile";
        var validFile = new ByteArrayResource("Content".getBytes());
        when(fileholder.exists(anyString()))
                .thenReturn(false);
        when(fileholder.saveFile(fileName, validFile))
                .thenReturn(true);

        var response = requestProcessor.proceedSaveFile(fileName, validFile, true);
        var body = response.getBody();
        assertEquals(String.format("File %s saved successfully on server", fileName), body);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(fileholder, atLeast(1)).saveFile(any(), any());
    }

    @Test
    public void proceedUploadFileRequestShouldBeBadRequest() throws IOException {
        var fileName = "mockFile";
        var invalidFile = new ByteArrayResource(new byte[]{});
        when(fileholder.exists(fileName))
                .thenReturn(false);
        when(fileholder.saveFile(fileName, invalidFile))
                .thenReturn(false);

        var response = requestProcessor.proceedSaveFile(fileName, invalidFile, true);
        var body = response.getBody();
        assertEquals("File is either empty or absent, nothing to store", body);
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(fileholder, times(1)).saveFile(any(), any());
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
