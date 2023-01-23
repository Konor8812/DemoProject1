package com.illia.client.model.request.registry;


public enum OperationsRegistry {
    SORT("sort"),
    DELETE("delete");

    OperationsRegistry(String operation){
    }

    public static OperationsRegistry getOperation(String operation){
        if(operation != null){
            return OperationsRegistry.valueOf(operation);
        }
        return null;
    }
}
