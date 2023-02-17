package com.illia.client.model.request.entity;

import static com.illia.client.model.request.registry.OperationsRegistry.COUNT;

import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.model.request.registry.OperationsRegistry;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CountQueryEntity extends QueryEntity {

  private Map<AttributeRegistry, String> attributeValueMap;
  private AttributeRegistry[] attributesToShow;

  @Override
  public OperationsRegistry getOperation() {
    return COUNT;
  }

}
