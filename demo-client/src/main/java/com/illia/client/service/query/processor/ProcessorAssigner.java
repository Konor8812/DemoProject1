package com.illia.client.service.query.processor;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.service.query.QueryProcessingException;
import com.illia.client.service.query.processor.unit.AverageOperationProcessorUnit;
import com.illia.client.service.query.processor.unit.CountOperationProcessorUnit;
import com.illia.client.service.query.processor.unit.DeleteOperationProcessorUnit;
import com.illia.client.service.query.processor.unit.SortOperationProcessorUnit;
import java.util.List;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProcessorAssigner {

  @Autowired
  private SortOperationProcessorUnit sortOperationProcessorUnit;
  @Autowired
  private DeleteOperationProcessorUnit deleteOperationProcessorUnit;
  @Autowired
  private AverageOperationProcessorUnit averageOperationProcessorUnit;
  @Autowired
  private CountOperationProcessorUnit countOperationProcessorUnit;

  public BiFunction<List<IMDbMovieEntity>, QueryEntity, List<?>> assignProcessor(QueryEntity queryEntity) throws QueryProcessingException {
    switch (queryEntity.getOperation()) {
      case SORT:
        return sortOperationProcessorUnit::process;
      case DELETE:
        return deleteOperationProcessorUnit::process;
      case AVERAGE:
        return averageOperationProcessorUnit::process;
      case COUNT:
        return countOperationProcessorUnit::process;
      default:
        throw new QueryProcessingException("This operation cannot be performed!");
    }
  }
}
