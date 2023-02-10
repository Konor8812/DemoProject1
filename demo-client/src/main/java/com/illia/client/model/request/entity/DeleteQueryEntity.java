package com.illia.client.model.request.entity;

import static com.illia.client.model.request.registry.OperationsRegistry.DELETE;

import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.model.request.registry.OperationsRegistry;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteQueryEntity extends QueryEntity {

  private AttributeRegistry attribute;
  private String value;

  @Override
  public OperationsRegistry getOperation() {
    return DELETE;
  }

}

