package com.illia.client.service.processor_registry.processors;

import com.illia.client.model.IMDbMovieEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DeleteValuesOperationProcessor implements OperationProcessor {

    /**
     * Deletes all entities where 'attribute' = value
     * e. g.
     * attribute = "date", value = "4/9/39"
     * this deletes all entities where date = "4/9/39" and returns rest as List
     */

    @Override
    public List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, Map<String, String> params) throws NoSuchFieldException {
        var attribute = params.get("attribute");
        var value = params.get("value");

        Field attributeField;
        try {
            attributeField = IMDbMovieEntity.class.getDeclaredField(attribute);
            attributeField.setAccessible(true);
            return records.stream().filter(x -> {
                try {
                    var attributeValue = (String) attributeField.get(x);
                    return attributeValue.equals(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return false;
            }).collect(Collectors.toList());
        } catch (NoSuchFieldException e) {
            throw e;
        }

    }
}
