package com.illia.client.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.IMDbMovieParser;
import com.illia.client.model.request.QueryRequestEntity;
import com.illia.client.service.processor.ProcessorAssigner;
import com.illia.client.service.processor.unit.OperationProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    public void performOperationShouldReturnBadRequestWithInvalidParamsMsg() {
        var requestEntity = mock(QueryRequestEntity.class);
        when(requestEntity.getErrorMsg()).thenReturn("");
        var result = queryProcessingService.performOperation(requestEntity);
        var body = result.getBody();
        assertTrue(body instanceof String);
        assertTrue(result.getStatusCode().is4xxClientError());
        verifyNoInteractions(parser, holder, processorAssigner, fileHandlingService);
    }

    private QueryRequestEntity buildRequestEntity(String fileName,
                                                  String operation,
                                                  String attribute,
                                                  String shouldParse,
                                                  String limit,
                                                  String order,
                                                  String valueForDeleteOperation) {
        return new QueryRequestEntity(fileName, operation, attribute, shouldParse, limit, order, valueForDeleteOperation);
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @CsvSource({"true,true",
            "true,false",
            "false,true",
            "true,false"})
    public void performOperationTestShouldParseParam(boolean shouldParse, boolean fileHolderHoldsFile) {
        var mockRequestEntity = mock(QueryRequestEntity.class);
        when(mockRequestEntity.shouldParse())
                .thenReturn(shouldParse);

        var mockEntitiesList = mock(List.class);
        var mockPath = mock(Path.class);
        when(fileHandlingService.resolveFilePath(any()))
                .thenReturn(mockPath);
        when(holder.getEntities(any()))
                .thenReturn(mockEntitiesList);

        // heres question if try/catch for part of method test is ok
        var mockProcessor = mock(BiFunction.class);
        when(processorAssigner.assignProcessor(any()))
                .thenReturn(mockProcessor);
        when(mockProcessor.apply(any(), any()))
                .thenReturn(mockEntitiesList);

        var response = queryProcessingService.performOperation(mockRequestEntity);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        if (shouldParse) {
            verifyNoInteractions(holder);
            verify(fileHandlingService, times(1)).resolveFilePath(any());
            verify(parser, times(1)).parseFile(any());
        } else {
            verify(holder, times(1)).getEntities(any());
            if (fileHolderHoldsFile) {
                verifyNoInteractions(parser);
                verifyNoInteractions(fileHandlingService);
            } else {
                verify(fileHandlingService, times(1)).resolveFilePath(any());
                verify(parser, times(1)).parseFile(any());
            }
        }

        verify(processorAssigner, times(1)).assignProcessor(any());
        verify(mockProcessor, times(1)).apply(any(), any());
        assertEquals(mockEntitiesList, response.getBody());
    }

}
