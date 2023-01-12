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
    public void proceedDownloadFileRequestShouldReturnBadRequest() throws IOException {
        when(fileholder.getFile("existingFile"))
                .thenReturn("Content".getBytes());

        var response = requestProcessor.proceedDownloadFile("notExistingFile");
        verify(fileholder, times(1)).getFile("notExistingFile");
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
    public void proceedUploadFileRequestWithOverwriteFlagTrue() throws IOException {
        var fileName = "fileName";
        var resource = mock(ByteArrayResource.class);
        when(fileholder.saveFile(fileName, resource))
                .thenReturn(true);

        var response = requestProcessor.proceedSaveFile(fileName, resource, true);
        var body = response.getBody();

        verify(fileholder, atLeast(1)).saveFile(fileName, resource);

        assertEquals(String.format("File %s saved successfully on server", fileName), body);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void proceedUploadExistingFileRequestWithOverwriteFlagFalse() throws IOException {
        var fileName = "fileName";
        var resource = mock(ByteArrayResource.class);
        when(fileholder.exists(fileName))
                .thenReturn(true);

        var response = requestProcessor.proceedSaveFile(fileName, resource, false);
        var body = response.getBody();
        assertEquals("File with such name already stored on server. " +
                "Consider adding overwrite=true request header to overwrite existing file or change file name", body);
        assertTrue(response.getStatusCode().is4xxClientError());

        verify(fileholder, never()).saveFile(any(), any());
    }

    @Test
    public void proceedUploadInvalidFileRequestShouldBeBadRequest() throws IOException {
        var fileName = "fileName";
        var resource = mock(ByteArrayResource.class);
        when(fileholder.saveFile(fileName, resource))
                .thenReturn(false);

        var response = requestProcessor.proceedSaveFile(fileName, resource, true);
        var body = response.getBody();
        assertEquals("File is either empty or absent, nothing to store", body);
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(fileholder, times(1)).saveFile(fileName, resource);
    }


}
