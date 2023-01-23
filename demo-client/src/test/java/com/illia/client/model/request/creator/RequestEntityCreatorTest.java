package com.illia.client.model.request.creator;

import com.illia.client.model.request.entity.DeleteQueryEntity;
import com.illia.client.model.request.entity.SortQueryEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class RequestEntityCreatorTest {

    @Test
    public void shouldReturnInvalidFileNameResponse() {
        var requestParams = new RequestParams();
        requestParams.addParam("FileName", "");
        var response = requestParams.createQueryEntity();
        assertEquals("File name isn't specified!", response);
    }

    @Test
    public void shouldReturnInvalidShouldParseParam() {
        var requestParams = new RequestParams();
        requestParams.addParam("FileName", "fileName");
        requestParams.addParam("shouldParse", "invalidValue");
        var response = requestParams.createQueryEntity();
        assertEquals("ShouldParse file param isn't specified!", response);
    }

    @Test
    public void shouldReturnInvalidAttributeResponse() {
        var requestParams = new RequestParams();
        requestParams.addParam("FileName", "fileName");
        requestParams.addParam("ShouldParse", "true");
        requestParams.addParam("Attribute", "invalidAttribute");
        var response = requestParams.createQueryEntity();
        assertEquals("There's no such attribute!", response);
    }

    @Test
    public void shouldReturnInvalidOperationResponse() {
        var requestParams = new RequestParams();
        requestParams.addParam("FileName", "fileName");
        requestParams.addParam("ShouldParse", "true");
        requestParams.addParam("Attribute", "DATE");
        requestParams.addParam("Operation", null);
        var response = requestParams.createQueryEntity();
        assertEquals("This operation isn`t supported!", response);
    }

    @ParameterizedTest
    @CsvSource({"invalid,4,Order for sorting isn't specified!",
            "ASC,invalid,Limit for sorting isn't specified!"})
    public void testSortOperationWithInvalidParams(String order, String limit, String expectedResponse) {
        var requestParams = new RequestParams();
        requestParams.addParam("FileName", "fileName");
        requestParams.addParam("ShouldParse", "true");
        requestParams.addParam("Attribute", "DATE");
        requestParams.addParam("Operation", "SORT");
        requestParams.addParam("Order", order);
        requestParams.addParam("Limit", limit);
        var response = requestParams.createQueryEntity();
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testDeleteOperationWithInvalidParams() {
        var requestParams = new RequestParams();
        requestParams.addParam("FileName", "fileName");
        requestParams.addParam("ShouldParse", "true");
        requestParams.addParam("Attribute", "DATE");
        requestParams.addParam("Operation", "DELETE");
        requestParams.addParam("ValueToDelete", null);
        var response = requestParams.createQueryEntity();
        assertEquals("Value for deleting isn't specified!", response);
    }

    @Test
    public void testShouldCreateSortRequestEntity() {
        var requestParams = new RequestParams();
        requestParams.addParam("FileName", "fileName");
        requestParams.addParam("ShouldParse", "true");
        requestParams.addParam("Attribute", "DATE");
        requestParams.addParam("Operation", "SORT");
        requestParams.addParam("Order", "ASC");
        requestParams.addParam("Limit", "1");
        var response = requestParams.createQueryEntity();
        assertTrue(response instanceof SortQueryEntity);
    }

    @Test
    public void testShouldCreateDeleteRequestEntity() {
        var requestParams = new RequestParams();
        requestParams.addParam("FileName", "fileName");
        requestParams.addParam("ShouldParse", "true");
        requestParams.addParam("Attribute", "DATE");
        requestParams.addParam("Operation", "DELETE");
        requestParams.addParam("ValueToDelete", "someValue");
        var response = requestParams.createQueryEntity();
        assertTrue(response instanceof DeleteQueryEntity);
    }

}
