package com.illia.client.service.processor.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.holder.IMDbMovieHolderImpl;
import com.illia.client.model.request.entity.DeleteQueryEntity;
import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.service.query.processor.unit.DeleteOperationProcessorUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {DeleteOperationProcessorUnit.class, IMDbMovieHolderImpl.class})
public class DeleteValuesOperationProcessorUnitTest {

  @Autowired
  DeleteOperationProcessorUnit deleteOperationProcessorUnit;
  @MockBean
  IMDbMovieHolderImpl holder;

  @ParameterizedTest
  @CsvSource({"Color,3",
      "Black and White,1"})
  public void testShouldDeleteByColor(String value, int entitiesToDeleteAmount) {
    var given = new ArrayList<IMDbMovieEntity>();
    given.add(IMDbMovieEntity.builder().color("Color").build());
    given.add(IMDbMovieEntity.builder().color("Black and White").build());
    given.add(IMDbMovieEntity.builder().color("Color").build());
    given.add(IMDbMovieEntity.builder().color("Color").build());
    given.add(IMDbMovieEntity.builder().color("Other").build());
    given.add(IMDbMovieEntity.builder().color(null).build());
    var queryRequestEntity = DeleteQueryEntity.builder()
        .attribute(AttributeRegistry.COLOR)
        .value(value)
        .build();
    List<?> result = deleteOperationProcessorUnit.process(given, queryRequestEntity);
    assertEquals(given.size() - entitiesToDeleteAmount, result.size());
    result.forEach(x -> assertNotEquals(value, ((IMDbMovieEntity)x).getColor()));
    verify(holder, times(1)).applyChanges(any());
  }

}
