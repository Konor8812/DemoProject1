package com.illia.client.service;

import com.illia.client.config.ClientConfig;
import com.illia.client.service.FileHandlingService;
import com.illia.client.service.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {FileUtil.class, FileHandlingService.class, ClientConfig.class})
public class FileHandlingServiceTest {

    @Autowired
    FileHandlingService fileHandlingService;
    @MockBean
    ClientConfig clientConfig;

    @MockBean
    FileUtil fileUtil;

    @Test
    public void resolveMultipartFileShouldCallFileHandlingService() throws IOException {
        var mockMultipartFile = mock(MultipartFile.class);
        fileHandlingService.resolveMultipartFile(mockMultipartFile);
        verify(fileUtil, times(1)).resolveMultipartFile(mockMultipartFile);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void saveFileTestShouldCallFileHandlingService(boolean overwriteFlag) throws IOException {
        var fileName = "fileName";
        var content = new byte[]{};
        when(clientConfig.getDownloadedFilesDirectoryPrefix())
                .thenReturn("directoryPath/");
        var expectedPath = Path.of(clientConfig.getDownloadedFilesDirectoryPrefix(), fileName);

        fileHandlingService.saveFile(fileName, content, overwriteFlag);
        if (overwriteFlag) {
            verify(fileUtil, times(1)).deleteFileIfExists(expectedPath);
        }
        verify(fileUtil, times(1)).saveFile(expectedPath, content);
    }

    @Test
    public void deleteExistingFileShouldCallFileHandlingServiceDeleteFileMethod() throws IOException {
        when(fileUtil.exists(notNull()))
                .thenReturn(true);
        fileHandlingService.deleteFile("fileName");
        verify(fileUtil, times(1)).exists(notNull());
        verify(fileUtil, times(1)).deleteFileIfExists(notNull());
    }

    @Test
    public void deleteNonExistingFileShouldNotCallFileHandlingServiceDeleteFileMethod() throws IOException {
        when(fileUtil.exists(any()))
                .thenReturn(false);
        fileHandlingService.deleteFile("fileName");
        verify(fileUtil, times(1)).exists(any());
        verify(fileUtil, never()).deleteFileIfExists(notNull());
    }
}
