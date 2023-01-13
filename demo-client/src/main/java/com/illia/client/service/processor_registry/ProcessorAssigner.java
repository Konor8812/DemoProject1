package com.illia.client.service.processor_registry;

import com.illia.client.service.processor_registry.processors.OperationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


import java.util.stream.Stream;

@Slf4j
@Component
public class ProcessorAssigner {

    @Autowired
    ApplicationContext applicationContext;

    public OperationProcessor assignProcessor(String operation) {
        try{
            return Stream.of(OperationsRegistry.values())
                    .filter(x -> x.getOperation().equals(operation))
                    .findFirst()
                    .map(operationsRegistry ->
                            applicationContext.getBean(operationsRegistry.getProcessorName(), OperationProcessor.class))
                    .orElse(null);
        }catch (Exception ex){
            log.error("Error during operation processor assigning ", ex);
            throw ex;
        }
    }
}
