package com.illia.server.file;

import com.illia.server.config.ServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {FileHolderImpl.class})
public class FileHolderTest {

    @Autowired
    FileHolderImpl fileHolder;
    @MockBean
    ServerConfig serverConfig;

    @MockBean
    FileUtil fileUtil;

    @Test
    public void testSaveValidFile() throws IOException {
        String fileName = "provided.csv";
        var filePath = Path.of(serverConfig.getSavedFilesDirectory(), fileName);
        var resource = mock(ByteArrayResource.class);
        when(fileUtil.validateResource(resource))
                .thenReturn(true);
        when(fileUtil.saveFile(filePath, resource)).thenReturn(true);

        var response = fileHolder.saveFile(fileName, resource);

        verify(fileUtil, times(1)).validateResource(resource);
        verify(fileUtil, times(1)).saveFile(filePath, resource);
        assertTrue(response);
        assertEquals(1, fileHolder.getFilesAmount());
    }

    @Test
    public void testSaveInvalidFile() throws IOException {
        var resource = mock(ByteArrayResource.class);
        when(fileUtil.validateResource(resource))
                .thenReturn(false);
        var response = fileHolder.saveFile("fileName", resource);
        verify(fileUtil, times(1)).validateResource(resource);
        verify(fileUtil, never()).saveFile(any(), any());
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
        when(fileUtil.validateResource(resource))
                .thenReturn(true);
        when(fileUtil.saveFile(filePath, resource))
                .thenReturn(true);
        when(fileUtil.getFileContent(filePath))
                .thenReturn(expected);

        fileHolder.saveFile(fileName, resource);
        assertEquals(1, fileHolder.getFilesAmount());

        var actual = fileHolder.getFile(fileName);

        verify(fileUtil, times(1)).getFileContent(filePath);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetFileShouldNotCallFileHandlerMethod() throws IOException {
        String fileName = "nonExistingFile";
        var resp = fileHolder.getFile(fileName);
        verify(fileUtil, never()).getFileContent(any());
        assertNull(resp);
    }

    @BeforeEach
    public void clearFileHolder(){
        fileHolder.savedFiles.clear();
    }
}