package com.illia.client.service.processor.unit;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.request.QueryRequestEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {DeleteOperationProcessorUnit.class, IMDbMovieHolderImpl.class})
public class DeleteValuesOperationProcessorUnitTest {

    @Autowired
    DeleteOperationProcessorUnit deleteOperationProcessorUnit;
    @MockBean
    IMDbMovieHolderImpl holder;

    @ParameterizedTest
    @CsvSource({"Color,3",
            "Black and White,1"})
    public void testShouldDeleteByColor(String color, int entitiesToDeleteAmount) {
        var given = new ArrayList<IMDbMovieEntity>();
        given.add(IMDbMovieEntity.builder().color("Color").build());
        given.add(IMDbMovieEntity.builder().color("Black and White").build());
        given.add(IMDbMovieEntity.builder().color("Color").build());
        given.add(IMDbMovieEntity.builder().color("Color").build());
        given.add(IMDbMovieEntity.builder().color("Other").build());
        given.add(IMDbMovieEntity.builder().color(null).build());
        var queryRequestEntity = new QueryRequestEntity("file1",
                "delete",
                "color",
                "true",
                "limit",
                "order",
                color);
        var result = deleteOperationProcessorUnit.proceed(given, queryRequestEntity);
        assertEquals(given.size() - entitiesToDeleteAmount, result.size());
        result.forEach(x -> assertNotEquals(color, x.getColor()));
        verify(holder, times(1)).applyChanges(result);
    }

}
