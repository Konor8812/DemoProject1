package com.illia.client.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class QueryRequestEntity {

    /**
     * fileName | required
     * operation | required, implemented by now: [delete, sort]
     * attribute | required
     * shouldParse | not required, default false
     * order | not required, default asc
     * amount | not required, returns everything by default
     */

    private String fileName;
    private String operation;
    private String attribute;
    private boolean shouldParse;
    private long limit;
    private String order;

    private String errorMsg = null;

    @JsonCreator
    public QueryRequestEntity(@JsonProperty(value = "fileName") String fileName,
                              @JsonProperty(value = "operation") String operation,
                              @JsonProperty(value = "attribute") String attribute,
                              @JsonProperty(value = "shouldParse", defaultValue = "false") String shouldParse,
                              @JsonProperty(value = "limit", defaultValue = "0x7fffffffffffffffL") String limit,
                              @JsonProperty(value = "order", defaultValue = "asc") String order) {
        var validateParamsMsg = validateParams(fileName, operation, attribute);
        System.out.println(validateParamsMsg);
        if (validateParamsMsg.equals("ok")) {
            this.fileName = fileName;
            this.operation = operation;
            this.attribute = attribute;

            this.shouldParse = fixShouldParseParam(shouldParse);
            this.limit = fixLimitParam(limit);
            this.order = fixOrderParam(order);
        } else {
            errorMsg = validateParamsMsg;
        }

    }

    private String fixOrderParam(String order) {
        if(!order.equals("desc")){
            return "asc";
        }
        return order;
    }

    private long fixLimitParam(String limit) {
        try{
            var res = Long.parseLong(limit);
            if(res <= 0){
                return 1;
            }
            return res;
        }catch (Exception e){
            return Long.MAX_VALUE;
        }
    }

    private boolean fixShouldParseParam(String shouldParse) {
        return shouldParse.equals("true");
    }

    private String validateParams(String fileName, String operation, String attribute) {
        StringBuilder responseBuilder = new StringBuilder();

        if (fileName == null || fileName.isEmpty()) {
            responseBuilder.append(" File name is not specified!");
        }
        if (attribute == null || attribute.isEmpty()) {
            responseBuilder.append(" Attribute is not specified!");
        }
        if (operation == null || operation.isEmpty()) {
            responseBuilder.append(" Operation is not specified!");
        }
        return responseBuilder.length() == 0 ? "ok" : responseBuilder.toString();

    }

    public boolean shouldParse(){
        return shouldParse;
    }

}
