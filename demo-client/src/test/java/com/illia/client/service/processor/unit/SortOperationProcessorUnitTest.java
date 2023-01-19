package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.request.QueryRequestEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {SortOperationProcessorUnit.class, IMDbMovieHolderImpl.class})
public class SortOperationProcessorUnitTest {
    @Autowired
    SortOperationProcessorUnit sortOperationProcessorUnit;

    @MockBean
    IMDbMovieHolderImpl holder;

    @ParameterizedTest
    @ValueSource(strings = {"1", "3", "10"})
    public void testSortShouldApplyLimitWithoutModifications(String limit) {
        var queryRequestEntity = new QueryRequestEntity("file1",
                "sort",
                "title",
                "true",
                limit,
                "order",
                "");
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertTrue(response.size() <= Long.parseLong(limit));
    }


    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc", "anyOtherValue"})
    @NullSource
    public void testSortByStringAttributeShouldApplyOrder(String order) {
        var queryRequestEntity = prepareRequestWithOrder(order);
        queryRequestEntity.setAttribute("title");
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var expected = getFiveEntitiesWithTitleSortedByTitle(order == null || !order.equals("desc"));
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertEquals(expected, response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc", "anyOtherValue"})
    @NullSource
    public void testSortByIntegerAttributeShouldApplyOrder(String order) {
        var queryRequestEntity = prepareRequestWithOrder(order);
        queryRequestEntity.setAttribute("budget");
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var expected = getFiveEntitiesWithTitleSortedByTitle(order == null || !order.equals("desc"));
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertEquals(expected, response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc", "anyOtherValue"})
    @NullSource
    public void testSortByDecimalAttributeShouldApplyOrder(String order) {
        var queryRequestEntity = prepareRequestWithOrder(order);
        queryRequestEntity.setAttribute("rating");
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var expected = getFiveEntitiesWithTitleSortedByTitle(order == null || !order.equals("desc"));
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertEquals(expected, response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc", "anyOtherValue"})
    @NullSource
    public void testSortByDateAttributeShouldApplyOrder(String order) {
        var queryRequestEntity = prepareRequestWithOrder(order);
        queryRequestEntity.setAttribute("date");
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var expected = getFiveEntitiesWithTitleSortedByTitle(order == null || !order.equals("desc"));
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertEquals(expected, response);
    }

    private QueryRequestEntity prepareRequestWithOrder(String order) {
        return new QueryRequestEntity("file1",
                "sort",
                "title",
                "true",
                "4L",
                order,
                "");
    }

    private List<IMDbMovieEntity> getFiveEntitiesWithTitleInShuffledOrder() {
        var givenEntities = new ArrayList<IMDbMovieEntity>();
        givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe2")
                .budget("200")
                .date("1/2/10")
                .rating("2.2").build());
        givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe4")
                .budget("400")
                .date("1/4/10")
                .rating("4.2").build());
        givenEntities.add(IMDbMovieEntity.builder().title(null)
                .budget(null)
                .date(null)
                .rating(null).build());
        givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe1")
                .budget("100")
                .date("1/1/10")
                .rating("1.2").build());
        givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe3")
                .budget("300")
                .date("1/3/10")
                .rating("3.2").build());
        return givenEntities;
    }

    private List<IMDbMovieEntity> getFiveEntitiesWithTitleSortedByTitle(boolean naturalOrder) {
        var givenEntities = new ArrayList<IMDbMovieEntity>();
        if (naturalOrder) {
            givenEntities.add(IMDbMovieEntity.builder().title(null)
                    .budget(null)
                    .date(null)
                    .rating(null).build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe1")
                    .budget("100")
                    .date("1/1/10")
                    .rating("1.2").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe2")
                    .budget("200")
                    .date("1/2/10")
                    .rating("2.2").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe3")
                    .budget("300")
                    .date("1/3/10")
                    .rating("3.2").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe4")
                    .budget("400")
                    .date("1/4/10")
                    .rating("4.2").build());
        } else {
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe4")
                    .budget("400")
                    .date("1/4/10")
                    .rating("4.2").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe3")
                    .budget("300")
                    .date("1/3/10")
                    .rating("3.2").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe2")
                    .budget("200")
                    .date("1/2/10")
                    .rating("2.2").build());
            givenEntities.add(IMDbMovieEntity.builder().title("ShouldBe1")
                    .budget("100")
                    .date("1/1/10")
                    .rating("1.2").build());
            givenEntities.add(IMDbMovieEntity.builder().title(null)
                    .budget(null)
                    .date(null)
                    .rating(null).build());
        }
        return givenEntities;
    }
}
