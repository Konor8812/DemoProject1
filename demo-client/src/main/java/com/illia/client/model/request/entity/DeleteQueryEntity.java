package com.illia.client.model.request.entity;

import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.model.request.registry.OperationsRegistry;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteQueryEntity implements QueryEntity{

    private String fileName;
    private boolean shouldParse;

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
        return OperationsRegistry.DELETE;
    }
}
