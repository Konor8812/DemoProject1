package com.illia.client.service.processor;

import com.illia.client.model.request.QueryRequestEntity;
import com.illia.client.service.processor.unit.DeleteOperationProcessorUnit;
import com.illia.client.service.processor.unit.SortOperationProcessorUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ProcessorAssigner.class, SortOperationProcessorUnit.class, DeleteOperationProcessorUnit.class})
public class ProcessorAssignerTest {

    @Autowired
    ProcessorAssigner processorAssigner;

    @MockBean
    SortOperationProcessorUnit sortOperationProcessorUnit;
    @MockBean
    DeleteOperationProcessorUnit deleteOperationProcessorUnit;

    @ParameterizedTest
    @ValueSource(strings = {"delete", "sort"})
    public void testOperationAssigment(String operation){
        var mockEntity = mock(QueryRequestEntity.class);
        when(mockEntity.getOperation()).thenReturn(operation);
        var processorUnit = processorAssigner.assignProcessor(mockEntity);
        assertNotNull(processorUnit);
    }

}
