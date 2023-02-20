package com.illia.client.service.query.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.entity.CountQueryEntity;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.model.request.registry.AttributeRegistry;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CountOperationProcessorUnit implements OperationProcessor {

  @Override
  public List<?> process(List<IMDbMovieEntity> records, QueryEntity queryEntity) {
    CountQueryEntity countQueryEntity = (CountQueryEntity) queryEntity;
    var attribute = countQueryEntity.getAttribute();

    assert canGroupBy(attribute) : "Can't perform operation with this attribute!";

    return records.stream()
        .collect(Collectors.groupingBy(x -> x.getFieldAccessor(attribute)))
        .entrySet()
        .stream()
        .map(x -> x.getKey() + ": " + x.getValue().size())
        .sorted()
        .collect(Collectors.toList());
  }

  private boolean canGroupBy(AttributeRegistry attribute) {
    switch (attribute){
      case COLOR:
      case GENRE:
      case LANGUAGE:
      case COUNTRY:
      case RATING:
      case DIRECTOR_NAME:
      case IMDB_SCORE:
        return true;
      default: return false;
    }
  }
}
