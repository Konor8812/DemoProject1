package com.illia.server.request;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.illia.server.file.FileHolder;
import com.illia.server.file.model.FileEntity.FileDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;

@SpringBootTest(classes = {RequestProcessor.class})
public class RequestProcessorTest {

  @MockBean
  FileHolder fileholder;
  @Autowired
  RequestProcessor requestProcessor;

  private final FileDocument preparedFileDocument = FileDocument.builder()
      .name("fileName")
      .content("content".getBytes())
      .build();

  @Test
  public void proceedDownloadFileRequestRequestShouldCallFileHolder() throws RequestProcessorException {

    var fileName = "fileName";
    when(fileholder.getFile(eq(fileName)))
        .thenReturn(preparedFileDocument);
    assertSame(preparedFileDocument, requestProcessor.proceedDownloadFile(fileName));
    verify(fileholder, times(1)).getFile(eq(fileName));
  }

  @Test
  public void proceedUploadFileRequestWithOverwriteFlagTrue() throws RequestProcessorException {
    var fileName = "fileName";
    var resource = new ByteArrayResource("content".getBytes());

    assertEquals(String.format("File %s saved successfully on server", fileName),
        requestProcessor.proceedSaveFile(fileName, resource, true));

    verify(fileholder, never()).exists(any());
    verify(fileholder, times(1)).saveFile(eq(fileName), eq(resource));
  }

  @Test
  public void proceedUploadExistingFileRequestWithOverwriteFlagFalse() {
    var fileName = "fileName";
    var resource = new ByteArrayResource("content".getBytes());
    when(fileholder.exists(eq(fileName)))
        .thenReturn(true);

    var actualException = assertThrowsExactly(RequestProcessorException.class,
        () -> requestProcessor.proceedSaveFile(fileName, resource, false));

    assertEquals("File with such name already stored on server. " +
        "Consider adding overwrite=true request header to overwrite existing file or change file name",
        actualException.getMessage());

    verify(fileholder, never()).saveFile(any(), any());
  }

  @Test
  public void proceedGetFilesAmountRequestShouldReturnNumber() {
    when(fileholder.getFilesAmount())
        .thenReturn(1L);

    assertEquals(1, requestProcessor.getFilesAmount());
    verify(fileholder, times(1)).getFilesAmount();
  }

}
