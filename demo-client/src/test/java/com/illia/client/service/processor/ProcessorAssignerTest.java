package com.illia.client.service.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.model.request.registry.OperationsRegistry;
import com.illia.client.service.query.QueryProcessingException;
import com.illia.client.service.query.processor.ProcessorAssigner;
import com.illia.client.service.query.processor.unit.AverageOperationProcessorUnit;
import com.illia.client.service.query.processor.unit.CountOperationProcessorUnit;
import com.illia.client.service.query.processor.unit.DeleteOperationProcessorUnit;
import com.illia.client.service.query.processor.unit.SortOperationProcessorUnit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {ProcessorAssigner.class, SortOperationProcessorUnit.class, DeleteOperationProcessorUnit.class})
public class ProcessorAssignerTest {

  @Autowired
  ProcessorAssigner processorAssigner;

  @MockBean
  SortOperationProcessorUnit sortOperationProcessorUnit;
  @MockBean
  DeleteOperationProcessorUnit deleteOperationProcessorUnit;
  @MockBean
  CountOperationProcessorUnit countOperationProcessorUnit;
  @MockBean
  AverageOperationProcessorUnit averageOperationProcessorUnit;

  @ParameterizedTest
  @ValueSource(strings = {"DELETE", "SORT", "AVERAGE", "COUNT"})
  public void testOperationAssigment(String operation) throws QueryProcessingException {
    var queryEntity = mock(QueryEntity.class);
    when(queryEntity.getOperation())
        .thenReturn(OperationsRegistry.valueOf(operation));
    assertNotNull(processorAssigner.assignProcessor(queryEntity));
  }

}
