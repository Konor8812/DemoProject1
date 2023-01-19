package com.illia.client.service.processor.unit;


import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolder;
import com.illia.client.model.request.QueryRequestEntity;
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
    public List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, QueryRequestEntity requestEntity){
        var attribute = requestEntity.getAttribute();
        long limit = requestEntity.getLimit();
        var order = requestEntity.getOrder();
        boolean shouldOrderAsc = "asc".equals(order);

        var comparator = getComparator(attribute, shouldOrderAsc);
        var result = records.stream()
                .sorted((x1, x2) -> compare(x1.getFieldAccessor(attribute), x2.getFieldAccessor(attribute), comparator))
                .limit(limit)
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
        Comparator<Object> comparator = (o1, o2) -> {
            if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            }
            var doubleValue1 = Double.parseDouble((String) o1);
            var doubleValue2 = Double.parseDouble((String) o2);
            return Double.compare(doubleValue1, doubleValue2);
        };
        return shouldOrderAsc ? comparator : comparator.reversed();
    }

    private Comparator<Object> getLongComparator(boolean shouldOrderAsc) {
        Comparator<Object> comparator = (o1, o2) -> {
            if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            }
            var longValue1 = Long.parseLong((String) o1);
            var longValue2 = Long.parseLong((String) o2);
            return Long.compare(longValue1, longValue2);
        };
        return shouldOrderAsc ? comparator : comparator.reversed();
    }

    private Comparator<Object> getStringComparator(boolean shouldOrderAsc) {
        Comparator<Object> comparator = (o1, o2) -> {
            var stringValue1 = (String) o1;
            var stringValue2 = (String) o2;
            if (stringValue1 == null || stringValue1.isEmpty()) {
                return -1;
            } else if (stringValue2 == null || stringValue2.isEmpty()) {
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
            if (stringValue1 == null || stringValue1.isEmpty()) {
                return -1;
            } else if (stringValue2 == null || stringValue2.isEmpty()) {
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
