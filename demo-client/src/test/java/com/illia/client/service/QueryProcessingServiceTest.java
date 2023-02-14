package com.illia.client.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.illia.client.model.holder.IMDbMovieHolderImpl;
import com.illia.client.model.parser.IMDbMovieParser;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.file.FileHandlingService;
import com.illia.client.service.query.QueryProcessingException;
import com.illia.client.service.query.QueryProcessingService;
import com.illia.client.service.query.processor.ProcessorAssigner;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {
    QueryProcessingService.class,
    FileHandlingService.class,
    ProcessorAssigner.class,
    IMDbMovieParser.class,
    IMDbMovieHolderImpl.class})
public class QueryProcessingServiceTest {

  @Autowired
  QueryProcessingService queryProcessingService;

  @MockBean
  FileHandlingService fileHandlingService;
  @MockBean
  ProcessorAssigner processorAssigner;
  @MockBean
  IMDbMovieParser parser;
  @MockBean
  IMDbMovieHolderImpl holder;

  @Test
  public void performOperationShouldSolveFileHandlingExceptionAndThrowQueryProcessingException() throws FileHandlingException {
    var requestEntity = mock(QueryEntity.class);
    var exceptionMsg = "exceptionMsg";
    when(requestEntity.isShouldParse())
        .thenReturn(true);
    when(fileHandlingService.resolveFilePath(any()))
        .thenThrow(new FileHandlingException(exceptionMsg));

    var response = assertThrowsExactly(QueryProcessingException.class, () -> queryProcessingService.performOperation(requestEntity));

    assertEquals(exceptionMsg, response.getMessage());

    verifyNoInteractions(parser, holder, processorAssigner);
  }

  @Test
  public void performOperationEmptyHolderTestShouldThrowException() throws QueryProcessingException {
    var requestEntity = mock(QueryEntity.class);

    when(requestEntity.isShouldParse())
        .thenReturn(false);
    when(holder.getEntities(any()))
        .thenThrow(new QueryProcessingException("Local cache is empty!"));

    var response = assertThrowsExactly(QueryProcessingException.class, () -> queryProcessingService.performOperation(requestEntity));
    assertEquals("Local cache is empty!",
        response.getMessage());

    verify(holder, times(1)).getEntities(any());
    verifyNoInteractions(parser, fileHandlingService, processorAssigner);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void performOperationTestShouldCallParserAndProceed() throws QueryProcessingException, FileHandlingException {
    var requestEntity = mock(QueryEntity.class);
    var fileName = "fileName";

    when(requestEntity.getFileName())
        .thenReturn(fileName);
    when(requestEntity.isShouldParse())
        .thenReturn(true);
    when(fileHandlingService.resolveFilePath(fileName))
        .thenReturn(mock(Path.class));
    when(processorAssigner.assignProcessor(any()))
        .thenReturn(mock(BiFunction.class));

    queryProcessingService.performOperation(requestEntity);

    verify(fileHandlingService, times(1)).resolveFilePath(eq(fileName));
    verify(parser, times(1)).parseFile(any());
    verify(processorAssigner, times(1)).assignProcessor(any());
    verifyNoInteractions(holder);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void performOperationTestHoldsFileShouldBeOk() throws QueryProcessingException {
    var requestEntity = mock(QueryEntity.class);
    var fileName = "fileName";

    when(requestEntity.getFileName())
        .thenReturn(fileName);
    when(requestEntity.isShouldParse())
        .thenReturn(false);

    when(holder.getEntities(eq(fileName)))
        .thenReturn(mock(List.class));
    when(processorAssigner.assignProcessor(any()))
        .thenReturn(mock(BiFunction.class));
    queryProcessingService.performOperation(requestEntity);

    verify(holder, times(1)).getEntities(eq(fileName));
    verify(processorAssigner, times(1)).assignProcessor(any());
    verifyNoInteractions(parser, fileHandlingService);
  }


}
