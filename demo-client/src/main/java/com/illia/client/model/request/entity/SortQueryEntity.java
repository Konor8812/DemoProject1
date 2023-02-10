package com.illia.client.model.request.entity;

import static com.illia.client.model.request.registry.OperationsRegistry.SORT;

import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.model.request.registry.OperationsRegistry;
import com.illia.client.model.request.registry.OrderRegistry;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SortQueryEntity extends QueryEntity {

  private AttributeRegistry attribute;
  private OrderRegistry order;
  private long limit;

  @Override
  public OperationsRegistry getOperation() {
    return SORT;
  }
}
