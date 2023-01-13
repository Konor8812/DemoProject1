package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.processor.InvalidAttributeException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DeleteValuesOperationProcessorUnit implements OperationProcessor {

    /**
     * Deletes all entities where 'attribute' = value
     * e. g.
     * attribute = "date", value = "4/9/39"
     * this deletes all entities where date = "4/9/39" and returns rest as List
     */

    @Override
    public List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, Map<String, String> params) throws InvalidAttributeException {
        var attribute = params.get("attribute");
        if(!IMDbMovieEntity.isAttributeValid(attribute)){
            throw new InvalidAttributeException("No such attribute " + attribute);
        }
        var value = params.get("value");

        return records.stream()
                .filter(x -> x.getFieldAccessor(attribute).equals(value))
        .collect(Collectors.toList());

    }
}
