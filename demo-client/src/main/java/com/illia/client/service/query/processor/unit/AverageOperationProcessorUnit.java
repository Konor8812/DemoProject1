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

    assert attributeToFind.isANumber() : "Can't find average for " + attributeToFind;
    var attributeToGroup = averageQueryEntity.getAttributeToGroup();
    assert attributeToGroup.isOkForGroupBy() : "Can't group by this attribute!";

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

}
