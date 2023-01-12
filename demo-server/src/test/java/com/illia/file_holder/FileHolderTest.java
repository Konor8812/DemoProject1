package com.illia.file_holder;

import com.illia.server.config.ServerConfig;
import com.illia.server.file_holder.FileHandler;
import com.illia.server.file_holder.FileHolder;
import com.illia.server.file_holder.FileHolderImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.annotation.DirtiesContext;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {FileHolderImpl.class})
public class FileHolderTest {

    @Autowired
    FileHolder fileHolder;
    @MockBean
    ServerConfig serverConfig;

    @MockBean
    FileHandler fileHandler;

    @Test
    public void testSaveValidFile() throws IOException {
        String fileName = "provided.csv";
        when(serverConfig.getSavedFilesDirectory())
                .thenReturn("directoryPath");
        var filePath = Path.of(serverConfig.getSavedFilesDirectory(), fileName);
        var resource = mock(ByteArrayResource.class);
        when(fileHandler.validateResource(resource))
                .thenReturn(true);
        when(fileHandler.saveFile(filePath, resource)).thenReturn(true);

        var response = fileHolder.saveFile(fileName, resource);

        verify(fileHandler, times(1)).validateResource(resource);
        verify(fileHandler, times(1)).saveFile(filePath, resource);
        assertTrue(response);
        assertEquals(1, fileHolder.getFilesAmount());
    }

    @Test
    public void testSaveInvalidFile() throws IOException {
        var resource = mock(ByteArrayResource.class);
        when(fileHandler.validateResource(resource))
                .thenReturn(false);
        var response = fileHolder.saveFile("fileName", resource);
        verify(fileHandler, times(1)).validateResource(resource);
        verify(fileHandler, never()).saveFile(any(), any());
        assertFalse(response);
        assertEquals(0, fileHolder.getFilesAmount());
    }


    @Test
    public void testGetFileShouldCallFileHandlerMethods() throws IOException {
        String fileName = "existingFile";

        when(serverConfig.getSavedFilesDirectory())
                .thenReturn("directoryPath");

        var filePath = Path.of(serverConfig.getSavedFilesDirectory(), fileName);
        var expected = "Content".getBytes();

        var resource = mock(ByteArrayResource.class);
        when(fileHandler.validateResource(resource))
                .thenReturn(true);
        when(fileHandler.saveFile(filePath, resource))
                .thenReturn(true);
        when(fileHandler.getFileContent(filePath))
                .thenReturn(expected);

        fileHolder.saveFile(fileName, resource);
        assertEquals(1, fileHolder.getFilesAmount());

        var actual = fileHolder.getFile(fileName);

        verify(fileHandler, times(1)).getFileContent(filePath);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetFileShouldNotCallFileHandlerMethod() throws IOException {
        String fileName = "nonExistingFile";
        var resp = fileHolder.getFile(fileName);
        verify(fileHandler, never()).getFileContent(any());
        assertNull(resp);
    }

}
