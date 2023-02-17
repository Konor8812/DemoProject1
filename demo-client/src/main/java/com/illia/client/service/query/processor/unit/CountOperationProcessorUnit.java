package com.illia.client.service.query.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.entity.CountQueryEntity;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.model.request.registry.AttributeRegistry;
import java.util.Arrays;
import java.util.HashMap;
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

    var attributes = countQueryEntity.getAttributeValueMap();

    if (attributes.size() == 1 && attributes.containsValue("**")) {
      var attribute = attributes.keySet().toArray(new AttributeRegistry[1])[0];
      var map = new HashMap<String, Integer>();

      records.stream()
          .forEach(x -> map.merge(x.getFieldAccessor(attribute), 1, Integer::sum));
      return map.entrySet().stream()
          .map(x -> x.getKey() + "  " + x.getValue()).sorted().collect(Collectors.toList());

    } else {
      var temp = records.stream()
          .filter(x -> attributes.entrySet().stream()
              .allMatch(e -> x.getFieldAccessor(e.getKey()).equals(e.getValue())));

      var attributesToShow = countQueryEntity.getAttributesToShow();

      return temp.map(x -> {
        var sb = new StringBuilder();
        Arrays.stream(attributesToShow)
            .forEach(k -> sb.append(k).append(" - ").append(x.getFieldAccessor(k)).append("  "));
        return sb.append(System.lineSeparator()).toString();
      }).collect(Collectors.toList());
    }
  }
}
