package com.illia.client.service.processor;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.QueryRequestEntity;
import com.illia.client.service.processor.unit.DeleteOperationProcessorUnit;
import com.illia.client.service.processor.unit.SortOperationProcessorUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.function.BiFunction;

@Slf4j
@Component
public class ProcessorAssigner {

    @Autowired
    SortOperationProcessorUnit sortOperationProcessorUnit;
    @Autowired
    DeleteOperationProcessorUnit deleteOperationProcessorUnit;

    // ok?
    public BiFunction<List<IMDbMovieEntity>, QueryRequestEntity, List<IMDbMovieEntity>> assignProcessor(String operation) {
        operation = operation.toLowerCase();
        switch (operation){
            case "sort":
                return sortOperationProcessorUnit::proceed;
            case "delete":
                return deleteOperationProcessorUnit::proceed;

            default:
                return null;
        }

    }
}
