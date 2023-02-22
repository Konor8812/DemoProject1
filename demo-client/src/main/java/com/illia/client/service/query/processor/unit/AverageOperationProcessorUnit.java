package com.illia.client.service.query.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.entity.AverageQueryEntity;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.model.request.registry.AttributeRegistry;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AverageOperationProcessorUnit implements OperationProcessor {

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

  @Override
  public List<?> process(List<IMDbMovieEntity> records, QueryEntity queryEntity) {
    AverageQueryEntity averageQueryEntity = (AverageQueryEntity) queryEntity;
    var attributeToFind = averageQueryEntity.getAttributeToFind();

    assert isANumber(attributeToFind) : "Can't find average for " + attributeToFind;
    var attributeToGroup = averageQueryEntity.getAttributeToGroup();
    assert canGroupBy(attributeToGroup) : "Can't group by this attribute!";

    return records.stream()
        .collect(Collectors.groupingBy(x -> x.getFieldAccessor(attributeToGroup)))
        .entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, x -> x.getValue().stream()
            .map(e -> e.getFieldAccessor(attributeToFind))
            .mapToDouble(Double::parseDouble).average()))
        .entrySet().stream()
        .map(x -> "Average for " + x.getKey() + " = " + DECIMAL_FORMAT.format(x.getValue().getAsDouble()))
        .collect(Collectors.toList());

  }

  private boolean isANumber(AttributeRegistry attributeToFind) {
    switch (attributeToFind) {
      case LEAD_ACTOR_FB_LIKES:
      case CAST_FB_LIKES:
      case DIRECTOR_FB_LIKES:
      case MOVIE_FB_LIKES:
      case IMDB_SCORE:
      case TOTAL_REVIEWS:
      case DURATION:
      case GROSS_REVENUE:
      case BUDGET:
        return true;
      default:
        return false;
    }
  }

  private boolean canGroupBy(AttributeRegistry attribute) {
    switch (attribute) {
      case COLOR:
      case GENRE:
      case LANGUAGE:
      case COUNTRY:
      case RATING:
      case DIRECTOR_NAME:
      case IMDB_SCORE:
        return true;
      default:
        return false;
    }
  }

}
