package com.illia.client.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.illia.client.config.ClientConfig;
import com.illia.client.model.file.FileEntity;
import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.file.FileHandlingService;
import com.illia.client.service.file.FileUtils;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(classes = {FileUtils.class, FileHandlingService.class, ClientConfig.class})
public class FileHandlingServiceTest {

  @Autowired
  FileHandlingService fileHandlingService;
  @MockBean
  ClientConfig clientConfig;

  @MockBean
  FileUtils fileUtils;

  @Test
  public void resolveMultipartFileShouldCallFileUtils() {
    var mockMultipartFile = mock(MultipartFile.class);
    fileHandlingService.resolveMultipartFile(mockMultipartFile);
    verify(fileUtils, times(1)).resolveMultipartFile(mockMultipartFile);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  public void saveFileTestShouldCallFileUtilsMethod(boolean overwriteFlag) {
    var fileName = "fileName";
    var content = new byte[]{};
    var preparedFileEntity = FileEntity.builder()
        .name(fileName)
        .content(content)
        .build();
    when(clientConfig.getDownloadedFilesDirectoryPrefix())
        .thenReturn("directoryPath/");
    var expectedPath = Path.of(clientConfig.getDownloadedFilesDirectoryPrefix(), fileName);

    fileHandlingService.saveFile(preparedFileEntity, overwriteFlag);
    if (overwriteFlag) {
      verify(fileUtils, times(1)).deleteFileIfExists(expectedPath);
    }
    verify(fileUtils, times(1)).saveFile(expectedPath, content);
  }


  @Test
  public void resolvePathTestShouldReturnNotNull() throws FileHandlingException {
    when(fileUtils.exists(notNull()))
        .thenReturn(true);
    assertNotNull(fileHandlingService.resolveFilePath(""));
    verify(fileUtils, times(1)).exists(any());
  }

  @Test
  public void resolveFilePathTestShouldThrowFileHandlingException() {
    var fileName = "fileName";
    when(fileUtils.exists(notNull()))
        .thenReturn(false);
    var res = assertThrowsExactly(FileHandlingException.class,
        () -> fileHandlingService.resolveFilePath(fileName));
    verify(fileUtils, times(1)).exists(any());
    assertEquals(String.format("File '%s' isn't found", fileName), res.getMessage());
  }

  @Test
  public void existsTestShouldReturnFalse() {
    when(fileUtils.exists(any()))
        .thenReturn(false);
    assertFalse(fileHandlingService.exists(null));
  }


  @Test
  public void deleteExistingFileShouldCallFileHandlingServiceDeleteFileMethod() throws FileHandlingException {
    when(fileUtils.exists(any()))
        .thenReturn(true);
    fileHandlingService.deleteFile("");
    verify(fileUtils, times(1))
        .deleteFileIfExists(any());
  }

  @Test
  public void deleteNonExistingFileShouldThrowFileHandlingException() {
    var fileName = "fileName";
    when(fileUtils.exists(any()))
        .thenReturn(false);
    var res = assertThrowsExactly(FileHandlingException.class,
        () -> fileHandlingService.deleteFile(fileName));
    verify(fileUtils, never())
        .deleteFileIfExists(any());
    assertEquals(String.format("File '%s' isn't found", fileName), res.getMessage());
  }
}
