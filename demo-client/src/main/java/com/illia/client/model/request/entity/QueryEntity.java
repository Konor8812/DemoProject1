package com.illia.client.model.request.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.illia.client.model.request.registry.OperationsRegistry;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        property = "operation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SortQueryEntity.class, name = "SORT"),
        @JsonSubTypes.Type(value = DeleteQueryEntity.class, name = "DELETE")
})
@Data
public abstract class QueryEntity {
    protected String fileName;
    protected boolean shouldParse;

    public String getFileName(){
        return fileName;
    }

    public boolean shouldParse(){
        return shouldParse;
    }

    abstract public OperationsRegistry getOperation();

}
