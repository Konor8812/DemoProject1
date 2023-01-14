package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.processor.OperationProcessorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = DeleteOperationProcessorUnit.class)
public class DeleteValuesOperationProcessorUnitTest {

    @Autowired
    DeleteOperationProcessorUnit deleteOperationProcessorUnit;

    @Test
    public void testShouldThrowExceptionWithInvalidAttributeReason(){
        var paramsMap = new HashMap<String, String>();
        paramsMap.put("attribute", "invalidAttribute");
        var ex = assertThrowsExactly(OperationProcessorException.class, () -> {
            deleteOperationProcessorUnit.proceed(mock(List.class), paramsMap);
        });
        assertEquals("No such attribute invalidAttribute", ex.getMessage());
    }

    @Test
    public void testShouldThrowExceptionWithEmptyListReason(){
        var ex = assertThrowsExactly(OperationProcessorException.class, () -> {
            deleteOperationProcessorUnit.proceed(null, null);
        });
        assertEquals("No records to proceed. This may be result of previous operations, consider reparsing file by setting reparse=true", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"Color,3", "Black and White,1"})
    public void testShouldDeleteByColor(String color, int entitiesToDeleteAmount){
        var given = new ArrayList<IMDbMovieEntity>();
        given.add(IMDbMovieEntity.builder().color("Color").build());
        given.add(IMDbMovieEntity.builder().color("Black and White").build());
        given.add(IMDbMovieEntity.builder().color("Color").build());
        given.add(IMDbMovieEntity.builder().color("Color").build());
        given.add(IMDbMovieEntity.builder().color("Other").build());
        given.add(IMDbMovieEntity.builder().color(null).build());
        var paramsMap = new HashMap<String, String>();
        paramsMap.put("attribute", "color");
        paramsMap.put("value", color);
        var result = deleteOperationProcessorUnit.proceed(given, paramsMap);
        assertEquals(given.size() - entitiesToDeleteAmount, result.size());
        result.forEach(x -> assertNotEquals(color, x.getColor()));
    }

}
