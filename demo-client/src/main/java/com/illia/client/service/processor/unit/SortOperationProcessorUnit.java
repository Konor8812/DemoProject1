package com.illia.client.service.processor.unit;


import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.processor.InvalidAttributeException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SortOperationProcessorUnit implements OperationProcessor{

    @Override
    public List<IMDbMovieEntity> proceed(List<IMDbMovieEntity> records, Map<String, String> params) throws InvalidAttributeException {
        var attribute = params.get("attribute");

        if(!IMDbMovieEntity.isAttributeValid(attribute)){
            throw new InvalidAttributeException("No such attribute " + attribute);
        }

        var order = params.get("order");
        long limit;

        try {
            limit = Long.parseLong(params.get("limit"));
            if(limit < 0){
                limit = 1;
            }
        } catch (Exception e) {
            limit = Long.MAX_VALUE;
        }

        boolean shouldOrderAsc = !order.equals("desc");

        var comparator = getComparator(attribute, shouldOrderAsc);

        return records.stream()
                .sorted((x1, x2) -> compare(x1.getFieldAccessor(attribute), x2.getFieldAccessor(attribute), comparator))
                .limit(limit)
                .collect(Collectors.toList());
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
        return (o1, o2) -> {
            if (o1 == null) {
                return shouldOrderAsc ? -1 : 1;
            } else if (o2 == null) {
                return shouldOrderAsc ? 1 : -1;
            }

            var doubleValue1 = Double.parseDouble((String) o1);
            var doubleValue2 = Double.parseDouble((String) o2);
            return shouldOrderAsc ? Double.compare(doubleValue1, doubleValue2) : Double.compare(doubleValue2, doubleValue1);
        };
    }

    private Comparator<Object> getLongComparator(boolean shouldOrderAsc) {
        return (o1, o2) -> {
            if (o1 == null) {
                return shouldOrderAsc ? -1 : 1;
            } else if (o2 == null) {
                return shouldOrderAsc ? 1 : -1;
            }
            var longValue1 = Long.parseLong((String) o1);
            var longValue2 = Long.parseLong((String) o2);
            return shouldOrderAsc ? Long.compare(longValue1, longValue2) : Long.compare(longValue2, longValue1);
        };
    }

    private Comparator<Object> getStringComparator(boolean shouldOrderAsc) {
        return (o1, o2) -> {
            var stringValue1 = (String) o1;
            var stringValue2 = (String) o2;
            if (stringValue1 == null || stringValue1.isEmpty()) {
                return shouldOrderAsc ? -1 : 1;
            } else if (stringValue2 == null || stringValue2.isEmpty()) {
                return shouldOrderAsc ? 1 : -1;
            }

            return shouldOrderAsc? stringValue1.compareTo(stringValue2) : stringValue2.compareTo(stringValue1);
        };
    }

    private Comparator<Object> getDateComparator(boolean shouldOrderAsc) {

        var dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return (o1, o2) -> {
            var stringValue1 = (String) o1;
            var stringValue2 = (String) o2;

            try {
                var dateValue1 = dateFormat.parse(stringValue1);
                var dateValue2 = dateFormat.parse(stringValue2);
                return shouldOrderAsc? dateValue1.compareTo(dateValue2) : dateValue2.compareTo(dateValue1);
            } catch (Exception e) {
                return shouldOrderAsc ? -1 : 1;
            }
        };

    }

}
