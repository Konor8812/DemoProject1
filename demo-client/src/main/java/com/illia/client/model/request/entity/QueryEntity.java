package com.illia.client.model.request.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.illia.client.model.request.registry.OperationsRegistry;
import lombok.Getter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = "operation")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SortQueryEntity.class, name = "SORT"),
    @JsonSubTypes.Type(value = DeleteQueryEntity.class, name = "DELETE"),
    @JsonSubTypes.Type(value = AverageQueryEntity.class, name = "AVERAGE"),
    @JsonSubTypes.Type(value = CountQueryEntity.class, name = "COUNT")
})
@Getter
public abstract class QueryEntity {

  protected String fileName;
  protected boolean shouldParse;

  abstract public OperationsRegistry getOperation();
}
