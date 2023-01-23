package com.illia.client.service.processor;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.request.creator.RequestParams;
import com.illia.client.model.request.entity.QueryEntity;
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

    public BiFunction<List<IMDbMovieEntity>, QueryEntity, List<IMDbMovieEntity>> assignProcessor(QueryEntity queryEntity) {
        switch (queryEntity.getOperation()) {
            case SORT:
                return sortOperationProcessorUnit::proceed;
            case DELETE:
                return deleteOperationProcessorUnit::proceed;
            default:
                return null;
        }
    }
}
