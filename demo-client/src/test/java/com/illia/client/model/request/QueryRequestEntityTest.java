package com.illia.client.model.request;


import com.illia.client.service.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {FileUtil.class})
public class QueryRequestEntityTest {

    @ParameterizedTest
    @ValueSource(strings = "")
    @NullSource
    public void testOutputForInvalidFileName(String fileName)  {
        var queryRequestEntity =new QueryRequestEntity(fileName,
                "sort",
                "title",
                "true",
                "limit",
                "order",
                "");
        assertEquals(" File name is not specified!", queryRequestEntity.getErrorMsg());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid"})
    @NullSource
    public void testOutputForInvalidAttribute(String attribute)  {
        var queryRequestEntity =new QueryRequestEntity("fileName",
                "sort",
                attribute,
                "true",
                "limit",
                "order",
                "");
        assertEquals(" There's no such attribute!", queryRequestEntity.getErrorMsg());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid"})
    @NullSource
    public void testOutputForInvalidOperation(String operation)  {
        var queryRequestEntity =new QueryRequestEntity("fileName",
                operation,
                "title",
                "true",
                "limit",
                "order",
                "");
        assertEquals(" This operation is not supported!", queryRequestEntity.getErrorMsg());
    }

    @Test
    public void testOutputForInvalidParamsShouldConcatErrors()  {
        var queryRequestEntity =new QueryRequestEntity(null,
                null,
                null,
                "true",
                "limit",
                "order",
                "");
        assertTrue(queryRequestEntity.getErrorMsg().contains(" This operation is not supported!"));
        assertTrue(queryRequestEntity.getErrorMsg().contains(" There's no such attribute!"));
        assertTrue(queryRequestEntity.getErrorMsg().contains(" File name is not specified!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc", "", "invalid"})
    @NullSource
    public void testOrderParamShouldBeSolved(String order)  {
        var queryRequestEntity =new QueryRequestEntity("fileName",
                "sort",
                "title",
                "true",
                "limit",
                order,
                "");
        var expected = "desc".equals(order)? "desc" : "asc";
        var actual = queryRequestEntity.getOrder();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"10", "", "-10", "invalid"})
    @NullSource
    public void testLimitParamShouldBeSolved(String limit)  {
        var queryRequestEntity =new QueryRequestEntity("fileName",
                "sort",
                "title",
                "true",
                limit,
                "asc",
                "");
        var actual = queryRequestEntity.getLimit();
        if(limit != null) {
            switch (limit) {
                case "10":
                    assertEquals(10, actual);
                    break;
                case "-10":
                    assertEquals(1, actual);
                    break;
                default:
                    assertEquals(Long.MAX_VALUE, actual);
            }
        } else {
            assertEquals(Long.MAX_VALUE, actual);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false", "", "invalid"})
    @NullSource
    public void testShouldParseParamShouldBeSolved(String shouldParse)  {
        var queryRequestEntity =new QueryRequestEntity("fileName",
                "sort",
                "title",
                shouldParse,
                "limit",
                "asc",
                "");
        var expected = "true".equals(shouldParse);
        var actual = queryRequestEntity.shouldParse();
        assertEquals(expected, actual);
    }
}
