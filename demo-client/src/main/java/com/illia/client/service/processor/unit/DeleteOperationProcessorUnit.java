package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.processor.OperationProcessorException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class DeleteOperationProcessorUnit implements OperationProcessor {

    /**
     * Deletes all entities where 'attribute' = value
     * if value absent(null) deletes all entities where 'attribute' is null
     * e. g.
     * attribute = "date", value = "4/9/39"
     * this deletes all entities where date = "4/9/39" and returns rest as List
     */

    @Override
    public List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, Map<String, String> params) throws OperationProcessorException {
        if(records == null || records.isEmpty()){
            throw new OperationProcessorException("No records to proceed. This may be result of previous operations, consider reparsing file by setting reparse=true");
        }
        var attribute = params.get("attribute");
        if (!IMDbMovieEntity.isAttributeValid(attribute)) {
            throw new OperationProcessorException("No such attribute " + attribute);
        }
        var value = params.get("value");
        Predicate<IMDbMovieEntity> filterPredicate;
        if(value != null){
            filterPredicate = x -> {
                var attrValue = x.getFieldAccessor(attribute);
                if(attrValue != null){
                    return !attrValue.equals(value);
                }else {
                    return true;
                }
            };
        }else {
            filterPredicate = x -> {
                var attrValue = x.getFieldAccessor(attribute);
                return attrValue.isEmpty();
            };
        }
        return records.stream()
                .filter(filterPredicate)
                .collect(Collectors.toList());
    }
}
