package com.illia.client.model.request.entity;

import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.model.request.registry.OperationsRegistry;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.illia.client.model.request.registry.OperationsRegistry.DELETE;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class DeleteQueryEntity extends QueryEntity{
    private AttributeRegistry attribute;
    private String value;

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean shouldParse() {
        return shouldParse;
    }

    @Override
    public OperationsRegistry getOperation() {
        return DELETE;
    }

}

