package com.illia.client.service.query.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.entity.AverageQueryEntity;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.model.request.registry.AttributeRegistry;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
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
    var attributes = averageQueryEntity.getAttributeValueMap();
    var attributeToFind = averageQueryEntity.getAttributeToFind();

    if (attributes.isEmpty()) {
      //if(attributeToFind.isANumber())
      var map = new HashMap<AttributeRegistry, Pair<Double>>();
      records.stream()
          .forEach(x -> {
            map.merge(attributeToFind,
                new Pair<Double>(1,
                    Double.parseDouble(x.getFieldAccessor(attributeToFind))),
                (v1, v2) -> new Pair<>(v1.amount + 1, v1.getSum() + Double.parseDouble(x.getFieldAccessor(attributeToFind))));
          });

      return map.entrySet().stream()
          .map(x -> x.getKey() + "  " + DECIMAL_FORMAT.format(x.getValue().getSum() / x.getValue().getAmount())).collect(Collectors.toList());
    } else {
      var temp = records.stream()
          .filter(x -> attributes.entrySet().stream()
              .allMatch(e -> x.getFieldAccessor(e.getKey()).equals(e.getValue())))
          .mapToDouble(x -> Double.parseDouble(x.getFieldAccessor(attributeToFind))).average();

      return List.of(DECIMAL_FORMAT.format(temp));
    }
  }

  @Data
  @AllArgsConstructor
  private static class Pair<S> {

    private Integer amount;
    private S sum;
  }

}
