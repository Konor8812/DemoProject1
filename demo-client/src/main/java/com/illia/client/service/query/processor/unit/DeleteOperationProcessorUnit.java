package com.illia.client.service.query.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.holder.IMDbMovieHolder;
import com.illia.client.model.request.entity.DeleteQueryEntity;
import com.illia.client.model.request.entity.QueryEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteOperationProcessorUnit implements OperationProcessor {

  @Autowired
  private IMDbMovieHolder holder;

  /**
   * Deletes all entities where 'attribute' = value e. g. attribute = "date", value = "4/9/39" this deletes all entities where date = "4/9/39" and returns rest
   * as List
   */

  @Override
  public List<IMDbMovieEntity> process(List<IMDbMovieEntity> records, QueryEntity queryEntity) {
    var deleteQueryEntity = (DeleteQueryEntity) queryEntity;
    var attribute = deleteQueryEntity.getAttribute();
    var value = deleteQueryEntity.getValue();

    var result = records.stream()
        .filter(x -> !(value.equals(x.getFieldAccessor(attribute))))
        .collect(Collectors.toList());
    log.info("Input :{} entities, Output :{} entities, deleted :{}", records.size(), result.size(), records.size() - result.size());
    holder.applyChanges(result);
    return result;
  }
}