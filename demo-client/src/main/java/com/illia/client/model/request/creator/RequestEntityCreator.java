package com.illia.client.model.request.creator;


import com.illia.client.model.request.entity.DeleteQueryEntity;
import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.model.request.entity.SortQueryEntity;
import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.model.request.registry.OperationsRegistry;
import com.illia.client.model.request.registry.OrderRegistry;

import java.util.Map;

public class RequestEntityCreator {

    public static Object createQueryEntity(Map<String, String> params) {
        var fileName = params.get("FileName");
        if (fileName == null || fileName.isEmpty()) {
            return "File name isn't specified!";
        }

        var shouldParseParam = params.get("ShouldParse");
        if (!"true".equals(shouldParseParam) && !"false".equals(shouldParseParam)) {
            return "ShouldParse file param isn't specified!";
        }

        var attribute = AttributeRegistry.getAttributeValue(params.get("Attribute"));
        if (attribute == null) {
            return "There's no such attribute!";
        }


        var operation = OperationsRegistry.getOperation(params.get("Operation"));
        if (operation == null) {
            return "This operation isn`t supported!";
        }

        QueryEntity queryEntity = null;
        switch (operation) {
            case SORT:
                var order = OrderRegistry.getOrderValue(params.get("Order"));
                if (order != null) {
                    try {
                        var limit = Long.parseLong(params.get("Limit"));
                        queryEntity = SortQueryEntity.builder()
                                .fileName(fileName)
                                .shouldParse(Boolean.parseBoolean(shouldParseParam))
                                .attribute(attribute)
                                .order(order)
                                .limit(limit)
                                .build();
                    } catch (NumberFormatException ex) {
                        return "Limit for sorting isn't specified!";
                    }
                } else {
                    return "Order for sorting isn't specified!";
                }
                break;
            case DELETE:
                var valueToDelete = params.get("ValueToDelete");
                if (valueToDelete != null) {
                    queryEntity = DeleteQueryEntity.builder()
                            .fileName(fileName)
                            .shouldParse(Boolean.parseBoolean(shouldParseParam))
                            .attribute(attribute)
                            .value(valueToDelete)
                            .build();
                } else {
                    return "Value for deleting isn't specified!";
                }
                break;
        }
        return queryEntity;
    }
}
