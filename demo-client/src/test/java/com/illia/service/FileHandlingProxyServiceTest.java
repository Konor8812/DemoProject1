package com.illia.service;

import com.illia.client.config.ClientConfig;
import com.illia.client.service.FileHandlingProxyService;
import com.illia.client.service.FileHandlingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {FileHandlingService.class, FileHandlingProxyService.class, ClientConfig.class})
public class FileHandlingProxyServiceTest {

    @Autowired
    FileHandlingProxyService fileHandlingProxyService;
    @MockBean
    ClientConfig clientConfig;

    @MockBean
    FileHandlingService fileHandlingService;

    @Test
    public void resolveMultipartFileShouldCallFileHandlingService() throws IOException {
        var mockMultipartFile = mock(MultipartFile.class);
        fileHandlingProxyService.resolveMultipartFile(mockMultipartFile);
        verify(fileHandlingService, times(1)).resolveMultipartFile(mockMultipartFile);
    }

    @Test
    public void saveFileTestShouldCallFileHandlingService() throws IOException {
        var fileName = "fileName";
        var content = new byte[]{};
        var overwriteFlag = new Random().nextBoolean(); // bad practice?
        when(clientConfig.getDownloadedFilesDirectoryPrefix())
                .thenReturn("directoryPath/");
        var expectedPath = Path.of(clientConfig.getDownloadedFilesDirectoryPrefix(), fileName);

        fileHandlingProxyService.saveFile(fileName, content, overwriteFlag);
        if (overwriteFlag) {
            verify(fileHandlingService, times(1)).deleteFile(expectedPath);
        }
        verify(fileHandlingService, times(1)).saveFile(expectedPath, content);
    }

    @Test
    public void deleteExistingFileShouldCallFileHandlingServiceDeleteFileMethod() throws IOException {
        when(fileHandlingService.exists(notNull()))
                .thenReturn(true);
        fileHandlingProxyService.deleteFile("fileName");
        verify(fileHandlingService, times(1)).exists(notNull());
        verify(fileHandlingService, times(1)).deleteFile(notNull());
    }

    @Test
    public void deleteNonExistingFileShouldNotCallFileHandlingServiceDeleteFileMethod() throws IOException {
        when(fileHandlingService.exists(any()))
                .thenReturn(false);
        fileHandlingProxyService.deleteFile("fileName");
        verify(fileHandlingService, times(1)).exists(any());
        verify(fileHandlingService, never()).deleteFile(notNull());
    }
}
