package com.illia.client.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.processor.OperationsRegistry;
import lombok.Data;


@Data
public class QueryRequestEntity {

    /**
     * fileName | mandatory
     * operation | mandatory, implemented operations by now: [delete, sort]
     * attribute | mandatory
     * shouldParse | not required, instructs to parse file instead of taking cashed values default false
     * order | not required, used in SORT, default asc
     * amount | not required, used in SORT, returns everything by default
     * value | required for DELETE, ignored otherwise
     */

    private String fileName;
    private String operation;
    private String attribute;
    private boolean shouldParse;
    private long limit;
    private String order;
    private String valueForDeleteOperation;

    private String errorMsg = null;

    @JsonCreator
    public QueryRequestEntity(@JsonProperty(value = "fileName") String fileName,
                              @JsonProperty(value = "operation") String operation,
                              @JsonProperty(value = "attribute") String attribute,
                              @JsonProperty(value = "shouldParse", defaultValue = "false") String shouldParse,
                              @JsonProperty(value = "limit", defaultValue = "0x7fffffffffffffffL") String limit,
                              @JsonProperty(value = "order", defaultValue = "asc") String order,
                              @JsonProperty(value = "value") String valueForDeleteOperation) {
        var validateParamsMsg = validateAndSolveParams(
                fileName,
                operation,
                attribute,
                valueForDeleteOperation);
        if (validateParamsMsg.isEmpty()) {
            this.fileName = fileName;
            this.operation = operation;
            this.attribute = attribute;

            this.shouldParse = fixShouldParseParam(shouldParse);
            this.limit = solveLimitParam(limit);
            this.order = solveOrderParam(order);
            this.valueForDeleteOperation = valueForDeleteOperation;

        } else {
            errorMsg = validateParamsMsg;
        }

    }

    private String solveOrderParam(String order) {
        if (!"desc".equals(order)) {
            return "asc";
        }
        return order;
    }

    private long solveLimitParam(String limit) {
        try {
            var res = Long.parseLong(limit);
            if (res <= 0) {
                return 1;
            }
            return res;
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    private boolean fixShouldParseParam(String shouldParse) {
        return shouldParse.equals("true");
    }

    private String validateAndSolveParams(String fileName, String operation, String attribute, String valueForDeleteOperation) {
        StringBuilder responseBuilder = new StringBuilder();

        if (fileName == null || fileName.isEmpty()) {
            responseBuilder.append(" File name is not specified!");
        }

        if (!IMDbMovieEntity.isAttributeValid(attribute)) {
            responseBuilder.append(" There's no such attribute!");
        }

        if (!OperationsRegistry.isOperationSupported(operation)) {
            responseBuilder.append(" This operation is not supported!");
            if (operation.equalsIgnoreCase("delete") && valueForDeleteOperation.isEmpty()) {
                responseBuilder.append(" Value for delete operation is not specified!");
            }
        }
        return responseBuilder.length() == 0 ? "" : responseBuilder.toString();
    }


    public boolean shouldParse() {
        return shouldParse;
    }

}
