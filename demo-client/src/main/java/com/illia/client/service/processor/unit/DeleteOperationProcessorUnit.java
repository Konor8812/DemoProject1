package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolder;
import com.illia.client.model.request.QueryRequestEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeleteOperationProcessorUnit implements OperationProcessor {

    @Autowired
    IMDbMovieHolder holder;

    /**
     * Deletes all entities where 'attribute' = value
     * e. g.
     * attribute = "date", value = "4/9/39"
     * this deletes all entities where date = "4/9/39" and returns rest as List
     */

    @Override
    public List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, QueryRequestEntity requestEntity){
        var attribute = requestEntity.getAttribute();
        var value = requestEntity.getValueForDeleteOperation();

        Predicate<IMDbMovieEntity> filterPredicate;
        if (value != null) {
            filterPredicate = x -> !value.equals(x.getFieldAccessor(attribute));
        } else {
            filterPredicate = x -> !(x.getFieldAccessor(attribute) == null);
        }
        var result = records.stream()
                .filter(filterPredicate)
                .collect(Collectors.toList());
        log.info("Input :{} entities, Output :{} entities, deleted :{}", records.size(), result.size(), records.size() - result.size());
        holder.applyChanges(result);
        return result;
    }
}
