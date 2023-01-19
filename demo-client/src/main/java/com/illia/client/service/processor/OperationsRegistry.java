package com.illia.client.service.processor;


import java.util.Locale;

public enum OperationsRegistry {
    SORT("sort"),
    DELETE("delete");

    private final String operation;

    OperationsRegistry(String operation){
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public static boolean isOperationSupported(String operation){
        try{
            if (operation == null) {
                return false;
            }
            OperationsRegistry.valueOf(operation.toUpperCase());
            return true;
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return false;
        }
    }

}
