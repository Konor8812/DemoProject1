package com.illia.client.service.processor;

import com.illia.client.service.processor.unit.DeleteOperationProcessorUnit;
import com.illia.client.service.processor.unit.SortOperationProcessorUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = {ProcessorAssigner.class, SortOperationProcessorUnit.class, DeleteOperationProcessorUnit.class})
public class ProcessorAssignerTest {

    @Autowired
    ProcessorAssigner processorAssigner;

    @ParameterizedTest
    @ValueSource(strings = {"delete", "sort"})
    public void testValidOperationAssigment(String operation){
        var processorUnit = processorAssigner.assignProcessor(operation);
        assertNotNull(processorUnit);
    }

    @Test
    public void testInvalidOperationAssigment(){
        var operation = "invalidOperation";
        var processorUnit = processorAssigner.assignProcessor(operation);
        assertNull(processorUnit);
    }
}
