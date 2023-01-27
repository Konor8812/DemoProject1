package com.illia.client.service;

import com.illia.client.model.holder.IMDbMovieHolderImpl;
import com.illia.client.model.parser.IMDbMovieParser;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.service.file.FileHandlingService;
import com.illia.client.service.query.QueryProcessingException;
import com.illia.client.service.query.processor.ProcessorAssigner;
import com.illia.client.service.query.QueryProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.file.Path;
import java.util.List;
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
    public void performOperationTestShouldParseShouldThrowException() {
        var requestEntity = mock(QueryEntity.class);
        var fileName = "fileName";

        when(requestEntity.getFileName())
                .thenReturn(fileName);
        when(requestEntity.shouldParse())
                .thenReturn(true);
        when(fileHandlingService.resolveFilePath(fileName))
                .thenReturn(null);

        var response = assertThrowsExactly(QueryProcessingException.class, () -> {
            queryProcessingService.performOperation(requestEntity);
        });

        assertEquals("No such file!",
                response.getMessage());

        verify(fileHandlingService, times(1)).resolveFilePath(eq(fileName));
        verifyNoInteractions(parser, holder, processorAssigner);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void performOperationTestShouldParseShouldBeOk() throws QueryProcessingException {
        var requestEntity = mock(QueryEntity.class);
        var fileName = "fileName";

        when(requestEntity.getFileName())
                .thenReturn(fileName);
        when(requestEntity.shouldParse())
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

    @Test
    public void performOperationTestHoldsFileShouldThrowException() {
        var requestEntity = mock(QueryEntity.class);
        var fileName = "fileName";

        when(requestEntity.getFileName())
                .thenReturn(fileName);
        when(requestEntity.shouldParse())
                .thenReturn(false);

        when(holder.getEntities(eq(fileName)))
                .thenReturn(null);

        var response = assertThrowsExactly(QueryProcessingException.class, () -> {
            queryProcessingService.performOperation(requestEntity);
        });

        assertEquals("No data in local cache!",
                response.getMessage());

        verify(holder, times(1)).getEntities(eq(fileName));
        verifyNoInteractions(parser, fileHandlingService, processorAssigner);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void performOperationTestHoldsFileShouldBeOk() throws QueryProcessingException {
        var requestEntity = mock(QueryEntity.class);
        var fileName = "fileName";

        when(requestEntity.getFileName())
                .thenReturn(fileName);
        when(requestEntity.shouldParse())
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
