package com.illia.client.model.request.entity;

import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.model.request.registry.OperationsRegistry;
import com.illia.client.model.request.registry.OrderRegistry;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.illia.client.model.request.registry.OperationsRegistry.SORT;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class SortQueryEntity extends QueryEntity {
    private AttributeRegistry attribute;
    private OrderRegistry order;
    private long limit;

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
        return SORT;
    }
}
