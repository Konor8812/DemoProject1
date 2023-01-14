package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.service.processor.OperationProcessorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = SortOperationProcessorUnit.class)
public class SortOperationProcessorUnitTest {
    @Autowired
    SortOperationProcessorUnit sortOperationProcessorUnit;

    @Test
    public void testShouldThrowExceptionWithInvalidAttributeReason(){
        var paramsMap = new HashMap<String, String>();
        paramsMap.put("attribute", "invalidAttribute");
        var ex = assertThrowsExactly(OperationProcessorException.class,
                () -> sortOperationProcessorUnit.proceed(mock(List.class), paramsMap));
        assertEquals("No such attribute invalidAttribute", ex.getMessage());
    }

    @Test
    public void testShouldThrowExceptionWithEmptyListReason(){
        var ex = assertThrowsExactly(OperationProcessorException.class,
                () -> sortOperationProcessorUnit.proceed(null, null));
        assertEquals("No records to proceed. This may be result of previous operations, consider reparsing file by setting reparse=true", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "5", "10"})
    public void testSortShouldApplyLimitWithoutModifications(String limit){
        var paramsMap = new HashMap<String, String>();
        paramsMap.put("attribute", "title");
        paramsMap.put("limit", limit);
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var response = sortOperationProcessorUnit.proceed(givenEntities, paramsMap);
        assertTrue(response.size() <= Long.parseLong(limit));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-5", "0"})
    public void testSortShouldApplyModifiedLimit(String limit){
        var paramsMap = new HashMap<String, String>();
        paramsMap.put("attribute", "title");
        paramsMap.put("limit", limit);
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var response = sortOperationProcessorUnit.proceed(givenEntities, paramsMap);
        assertEquals(1, response.size());
    }

    @Test
    public void testSortWithInvalidLimitFormatShouldReturnSameValues(){
        var paramsMap = new HashMap<String, String>();
        paramsMap.put("attribute", "title");
        paramsMap.put("limit", "invalidNumberFormat");
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var response = sortOperationProcessorUnit.proceed(givenEntities, paramsMap);
        assertEquals(givenEntities.size(), response.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc", "anyOtherValue"})
    @NullSource
    public void testSortByTitleShouldApplyOrder(String order){
        var paramsMap = new HashMap<String, String>();
        paramsMap.put("attribute", "title");
        paramsMap.put("order", order);
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var expected = getFiveEntitiesWithTitleSortedByTitle(order == null || !order.equals("desc"));
        var response = sortOperationProcessorUnit.proceed(givenEntities, paramsMap);
        assertEquals(expected, response);
    }

    private List<IMDbMovieEntity> getFiveEntitiesWithTitleInShuffledOrder(){
        var givenEntities = new ArrayList<IMDbMovieEntity>();
        givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe2").build());
        givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe4").build());
        givenEntities.add(IMDbMovieEntity.builder().title(null).build());
        givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe1").build());
        givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe3").build());
        return givenEntities;
    }

    private List<IMDbMovieEntity> getFiveEntitiesWithTitleSortedByTitle(boolean naturalOrder){
        var givenEntities = new ArrayList<IMDbMovieEntity>();
        if(naturalOrder){
            givenEntities.add(IMDbMovieEntity.builder().title(null).build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe1").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe2").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe3").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe4").build());
        }else {
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe4").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe3").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe2").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe1").build());
            givenEntities.add(IMDbMovieEntity.builder().title(null).build());
        }
        return givenEntities;
    }
}
