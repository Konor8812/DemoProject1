package com.illia.client.model.request.entity;

import com.illia.client.model.request.registry.OperationsRegistry;

public interface QueryEntity {

    public String getFileName();
    public boolean shouldParse();
    public OperationsRegistry getOperation();
}
