package com.illia.client.service;

import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.IMDbMovieParser;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private final String fileName = "fileName";
    private final String operation = "someOperation";
    private final String attribute = "anyAttribute";

    @Test
    public void performOperationTestWithEmptyParamsMap() {
        Map<String, String> paramsMap = new HashMap<>();
        var result = queryProcessingService.performOperation(paramsMap).getBody();
        assertTrue(result instanceof String);
        var resultAsString = (String) result;
        assertTrue(resultAsString.contains("File name is not specified!"));
        assertTrue(resultAsString.contains("Attribute is not specified!"));
        assertTrue(resultAsString.contains("Operation is not specified!"));
        verifyNoInteractions(parser, holder, processorAssigner, fileHandlingService);
    }


    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void performOperationTestWithValidMandatoryParamsPresent(boolean fileHolderHoldsFile) {
        Map<String, String> paramsMap = getMandatoryParamsMap();

        var mockPath = mock(Path.class);
        var mockProcessor = mock(OperationProcessor.class);
        var mockEntitiesList = mock(List.class);

        when(holder.holdsFile(fileName))
                .thenReturn(fileHolderHoldsFile);
        when(processorAssigner.assignProcessor(operation))
                .thenReturn(mockProcessor);
        when(fileHandlingService.resolveFilePath(eq(fileName)))
                .thenReturn(mockPath);
        when(parser.parseFile(mockPath.toFile()))
                .thenReturn(mockEntitiesList);
        when(mockProcessor.proceed(mockEntitiesList, paramsMap))
                .thenReturn(mockEntitiesList);

        if(fileHolderHoldsFile){
            when(holder.getEntities())
                    .thenReturn(mockEntitiesList);
        }

        var response = queryProcessingService.performOperation(paramsMap);

        verify(holder, times(1)).holdsFile(eq(fileName));
        if(fileHolderHoldsFile){
            verify(holder, times(1)).getEntities();
        }else{
            verify(fileHandlingService, times(1)).resolveFilePath(eq(fileName));
            verify(parser, times(1)).parseFile(mockPath.toFile());
        }

        verify(processorAssigner, times(1)).assignProcessor(eq(operation));
        verify(mockProcessor, times(1)).proceed(mockEntitiesList, paramsMap);
        assertEquals(mockEntitiesList, response.getBody());
    }

    @ParameterizedTest
    @CsvSource({"false,false", "false,true", "true,false", "true,true", "invalidValue,true", "invalidValue,false"})
    public void performOperationTestParseParamWithDifferentFileHolderResponse(String shouldReparse, boolean fileHolderHoldsFile) {
        when(holder.holdsFile(fileName))
                .thenReturn(fileHolderHoldsFile);

        Map<String, String> paramsMap = getMandatoryParamsMap();
        paramsMap.put("shouldParse", shouldReparse);

        queryProcessingService.performOperation(paramsMap);

        if(shouldReparse.equals("false")){
            verify(holder, times(1)).holdsFile(fileName);
            if(fileHolderHoldsFile) {
                verify(holder, times(1)).getEntities();
            }else {
                verify(holder, never()).getEntities();
                verify(fileHandlingService, times(1)).resolveFilePath(eq(fileName));
            }
        }else{
            verify(holder, never()).holdsFile(any());
            verify(holder, never()).getEntities();
            verify(fileHandlingService, times(1)).resolveFilePath(eq(fileName));
        }

    }

    private Map<String, String> getMandatoryParamsMap(){
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("fileName", fileName);
        paramsMap.put("operation", operation);
        paramsMap.put("attribute", attribute);
        return paramsMap;
    }
}
