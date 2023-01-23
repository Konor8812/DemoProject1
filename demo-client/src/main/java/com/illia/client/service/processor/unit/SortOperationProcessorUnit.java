package com.illia.client.service.processor.unit;


import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolder;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.model.request.entity.SortQueryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SortOperationProcessorUnit implements OperationProcessor {
    @Autowired
    IMDbMovieHolder holder;
    /**
     * Sorts input entities list by 'attribute'
     * in 'order' order and
     * takes 'limit' elements
     */

    @Override
    public List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, QueryEntity queryEntity){
        var attribute = ((SortQueryEntity) queryEntity).getAttribute().getAttributeValue();
        boolean shouldOrderAsc = ((SortQueryEntity) queryEntity).getOrder().getOrderValue().equals("ASC");

        var comparator = getComparator(attribute, shouldOrderAsc);
        var result = records.stream()
                .sorted((x1, x2) -> compare(x1.getFieldAccessor(attribute), x2.getFieldAccessor(attribute), comparator))
                .limit(((SortQueryEntity) queryEntity).getLimit())
                .collect(Collectors.toList());

        log.info("Input :{} entities, Output :{} entities", records.size(), result.size());
        holder.applyChanges(result);
        return result;
    }

    private int compare(String x1, String x2, Comparator<Object> comparator) {
        return comparator.compare(x1, x2);
    }


    private Comparator<Object> getComparator(String attribute, boolean shouldGetMax) {
        switch (attribute) {
            case "IMBdScore":
                return getDoubleComparator(shouldGetMax);

            case "date":
                return getDateComparator(shouldGetMax);

            case "leadActorFBLikes":
            case "castFBLikes":
            case "directorFBLikes":
            case "movieFBLikes":
            case "totalReviews":
            case "duration":
            case "grossRevenue":
            case "budget":
                return getLongComparator(shouldGetMax);

            default:
                return getStringComparator(shouldGetMax);
        }
    }

    private Comparator<Object> getDoubleComparator(boolean shouldOrderAsc) {
        Comparator<Object> comparator = Comparator.comparingDouble(o -> Double.parseDouble((String) o));
        return shouldOrderAsc ? comparator : comparator.reversed();
    }

    private Comparator<Object> getLongComparator(boolean shouldOrderAsc) {
        Comparator<Object> comparator = Comparator.comparingLong(o -> Long.parseLong((String) o));
        return shouldOrderAsc ? comparator : comparator.reversed();
    }

    private Comparator<Object> getStringComparator(boolean shouldOrderAsc) {
        Comparator<Object> comparator = (o1, o2) -> {
            var stringValue1 = (String) o1;
            var stringValue2 = (String) o2;
            if (stringValue1.isBlank()) {
                return -1;
            } else if (stringValue2.isBlank()) {
                return 1;
            }
            return stringValue1.compareTo(stringValue2);
        };
        return shouldOrderAsc ? comparator : comparator.reversed();
    }

    private Comparator<Object> getDateComparator(boolean shouldOrderAsc) {
        Comparator<Object> comparator = (o1, o2) -> {
            var dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            var stringValue1 = (String) o1;
            var stringValue2 = (String) o2;
            if (stringValue1.isEmpty()) {
                return -1;
            } else if (stringValue2.isEmpty()) {
                return 1;
            }

            try {
                var dateValue1 = dateFormat.parse(stringValue1);
                var dateValue2 = dateFormat.parse(stringValue2);
                return dateValue1.compareTo(dateValue2);
            } catch (Exception e) {
                return shouldOrderAsc ? -1 : 1;
            }
        };
        return shouldOrderAsc ? comparator : comparator.reversed();
    }
}
