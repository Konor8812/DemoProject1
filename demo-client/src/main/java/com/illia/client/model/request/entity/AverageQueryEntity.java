package com.illia.client.model.request.entity;

import static com.illia.client.model.request.registry.OperationsRegistry.AVERAGE;

import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.model.request.registry.OperationsRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AverageQueryEntity extends QueryEntity{

  private AttributeRegistry attributeToGroup;
  private AttributeRegistry attributeToFind;

  @Override
  public OperationsRegistry getOperation() {
    return AVERAGE;
  }

}
