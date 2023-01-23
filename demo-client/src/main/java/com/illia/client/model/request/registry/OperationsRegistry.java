package com.illia.client.model.request.registry;


public enum OperationsRegistry {
    SORT("sort"),
    DELETE("delete");

    OperationsRegistry(String operation) {
    }

    public static OperationsRegistry getOperation(String operation) {
        if (operation != null) {
            try {
                return OperationsRegistry.valueOf(operation);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
        return null;
    }
}
