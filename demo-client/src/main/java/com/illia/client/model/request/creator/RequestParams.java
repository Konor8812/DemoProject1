package com.illia.client.model.request.creator;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class RequestParams {

    /**
     * FileName | file name
     * ShouldParse | instructs to parse file instead of taking cashed values
     * Operation | implemented operations by now: [delete, sort]
     * ~~~
     * SORT operation:
     * Attribute | attribute (field)
     * Order | specifies order [asc, desc]
     * Limit | specifies amount
     * ~~~
     * DELETE operation:
     * Attribute | attribute (field)
     * Value | specifies values to delete
     */

    private Map<String, String> params = new HashMap<>();

    @JsonAnySetter
    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public Object createQueryEntity(){
        return RequestEntityCreator.createQueryEntity(params);
    }

}
