package com.illia.client.service.processor;


public enum OperationsRegistry {
    SORT("sort", "sortOperationProcessorUnit"),
    DELETE("delete", "deleteValuesOperationProcessorUnit");

    private final String operation;
    private final String processorName;

    OperationsRegistry(String operation, String processorName){
        this.operation = operation;
        this.processorName = processorName;
    }

    public String getOperation() {
        return operation;
    }

    public String getProcessorName() {
        return processorName;
    }
}
