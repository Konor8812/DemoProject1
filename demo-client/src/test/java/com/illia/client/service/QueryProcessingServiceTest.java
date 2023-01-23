package com.illia.client.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.IMDbMovieParser;
import com.illia.client.model.request.entity.QueryEntity;
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

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void performOperationTestShouldParseParam(boolean shouldParse) {
        var requestEntity = mock(QueryEntity.class);
        var fileName = "fileName";

        when(requestEntity.getFileName())
                .thenReturn(fileName);
        when(requestEntity.shouldParse())
                .thenReturn(shouldParse);

        if (shouldParse) {
            var entitiesList = mock(List.class);
            var path = mock(Path.class);
            when(fileHandlingService.resolveFilePath(any()))
                    .thenReturn(path);
            when(parser.parseFile(eq(path.toFile())))
                    .thenReturn(entitiesList);
            when(processorAssigner.assignProcessor(eq(requestEntity)))
                    .thenReturn(mock(BiFunction.class));

            var response = queryProcessingService.performOperation(requestEntity);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            verify(fileHandlingService, times(1))
                    .resolveFilePath(eq(fileName));
            verify(parser, times(1))
                    .parseFile(path.toFile());
            verify(processorAssigner, times(1))
                    .assignProcessor(eq(requestEntity));

        } else {
            when(holder.getEntities(eq(fileName)))
                    .thenReturn(null);

            var response = queryProcessingService.performOperation(requestEntity);
            assertTrue(response.getStatusCode().is4xxClientError());
            assertEquals("No data in local cache!",
                    response.getBody());

            verify(holder, times(1)).getEntities(eq(fileName));
            verifyNoInteractions(parser, fileHandlingService, processorAssigner);
        }


    }


    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void performOperationTestFileHolderHoldsFile(boolean fileHolderHoldsFile) {
        var requestEntity = mock(QueryEntity.class);
        var fileName = "fileName";

        when(requestEntity.getFileName())
                .thenReturn(fileName);
        when(requestEntity.shouldParse())
                .thenReturn(false);

        when(holder.holdsFile(eq(fileName)))
                .thenReturn(fileHolderHoldsFile);

        if (fileHolderHoldsFile) {
            var entitiesList = mock(List.class);
            when(holder.getEntities(eq(fileName)))
                    .thenReturn(entitiesList);
            when(processorAssigner.assignProcessor(eq(requestEntity)))
                    .thenReturn(mock(BiFunction.class));

            var response = queryProcessingService.performOperation(requestEntity);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            verifyNoInteractions(parser, fileHandlingService);
            verify(processorAssigner, times(1))
                    .assignProcessor(requestEntity);
        } else {
            var path = mock(Path.class);
            when(fileHandlingService.resolveFilePath(eq(fileName)))
                    .thenReturn(path);
            when(holder.getEntities(eq(fileName)))
                    .thenReturn(null);

            var response = queryProcessingService.performOperation(requestEntity);
            assertTrue(response.getStatusCode().is4xxClientError());
            assertEquals("No data in local cache!",
                    response.getBody());
        }
        verify(holder, times(1)).getEntities(eq(fileName));
    }

}
