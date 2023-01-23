package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.request.entity.SortQueryEntity;
import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.model.request.registry.OrderRegistry;
import org.junit.jupiter.params.ParameterizedTest;
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
    @ValueSource(longs = {1, 3, 10})
    public void testSortShouldApplyLimitWithoutModifications(long limit) {
        var queryRequestEntity = buildRequestEntity("TITLE", "ASC", limit);
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertTrue(response.size() <= limit);
    }


    @ParameterizedTest
    @ValueSource(strings = {"ASC", "DESC"})
    public void testSortByStringAttributeShouldApplyOrder(String order) {
        var queryRequestEntity = buildRequestEntity("TITLE", order, 5);
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var expected = getFiveSortedEntities(order.equals("ASC"));
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertEquals(expected, response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ASC", "DESC"})
    public void testSortByIntegerAttributeShouldApplyOrder(String order) {
        var queryRequestEntity = buildRequestEntity("BUDGET", order, 5);
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var expected = getFiveSortedEntities(order.equals("ASC"));
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertEquals(expected, response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ASC", "DESC"})
    public void testSortByDecimalAttributeShouldApplyOrder(String order) {
        var queryRequestEntity = buildRequestEntity("RATING", order, 5);
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var expected = getFiveSortedEntities(order.equals("ASC"));
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertEquals(expected, response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ASC", "DESC"})
    public void testSortByDateAttributeShouldApplyOrder(String order) {
        var queryRequestEntity = buildRequestEntity("DATE", order, 5);
        var givenEntities = getFiveEntitiesWithTitleInShuffledOrder();
        var expected = getFiveSortedEntities(order.equals("ASC"));
        var response = sortOperationProcessorUnit.proceed(givenEntities, queryRequestEntity);
        verify(holder, times(1)).applyChanges(response);
        assertEquals(expected, response);
    }

    private SortQueryEntity buildRequestEntity(String attribute,
                                               String order,
                                               long limit) {
        return SortQueryEntity.builder()
                .attribute(AttributeRegistry.valueOf(attribute))
                .order(OrderRegistry.valueOf(order))
                .limit(limit)
                .build();
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
        givenEntities.add(IMDbMovieEntity.builder().title("")
                .budget("0")
                .date("")
                .rating(".0").build());
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

    private List<IMDbMovieEntity> getFiveSortedEntities(boolean naturalOrder) {
        var givenEntities = new ArrayList<IMDbMovieEntity>();
        if (naturalOrder) {
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("")
                    .budget("0")
                    .date("")
                    .rating(".0").build());
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("ShouldBe1")
                    .budget("100")
                    .date("1/1/10")
                    .rating("1.2").build());
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("ShouldBe2")
                    .budget("200")
                    .date("1/2/10")
                    .rating("2.2").build());
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("ShouldBe3")
                    .budget("300")
                    .date("1/3/10")
                    .rating("3.2").build());
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("ShouldBe4")
                    .budget("400")
                    .date("1/4/10")
                    .rating("4.2").build());
        } else {
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("ShouldBe4")
                    .budget("400")
                    .date("1/4/10")
                    .rating("4.2").build());
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("ShouldBe3")
                    .budget("300")
                    .date("1/3/10")
                    .rating("3.2").build());
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("ShouldBe2")
                    .budget("200")
                    .date("1/2/10")
                    .rating("2.2").build());
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("ShouldBe1")
                    .budget("100")
                    .date("1/1/10")
                    .rating("1.2").build());
            givenEntities.add(IMDbMovieEntity.builder()
                    .title("")
                    .budget("0")
                    .date("")
                    .rating(".0").build());
        }
        return givenEntities;
    }
}
