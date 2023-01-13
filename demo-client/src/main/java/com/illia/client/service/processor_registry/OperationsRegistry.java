package com.illia.client.service.processor_registry;


public enum OperationsRegistry {
    SORT("sort", "sortOperationProcessor"),
    MIN_VALUE("minValue", "minValueOperationProcessor"),
    MAX_VALUE("maxValue", "maxValueOperationProcessor"),
    DELETE("delete", "deleteValuesOperationProcessor");

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
