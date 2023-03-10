package com.illia.client.service.processor.unit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.holder.IMDbMovieHolderImpl;
import com.illia.client.model.parser.IMDbMovieParser;
import com.illia.client.model.request.entity.AverageQueryEntity;
import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.query.processor.unit.AverageOperationProcessorUnit;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {IMDbMovieParser.class, IMDbMovieHolderImpl.class, AverageOperationProcessorUnit.class})
public class AverageOperationProcessorUnitTest {

  @Autowired
  IMDbMovieParser parser;


  @Autowired
  AverageOperationProcessorUnit processorUnit;

  // kinda inconvenient test
  // my point is to evaluate average through 'obvious operations' and compare results with actual from processorUnit

  @ParameterizedTest
  @ValueSource(strings = {"COUNTRY", "GENRE"})
  public void testShouldGetAverageForDURATIONGroupingByAttribute(AttributeRegistry attributeToGroupBy) throws FileHandlingException, URISyntaxException {

    var entities = prepareEntitiesList();
    var queryEntity = AverageQueryEntity.builder()
        .attributeToGroup(attributeToGroupBy)
        .attributeToFind(AttributeRegistry.DURATION).build();

    var distinctAttributeValuesAmount = entities.stream()
        .map(a -> a.getFieldAccessor(attributeToGroupBy))
        .distinct()
        .collect(Collectors.toList());

    var expectedResultMap = new HashMap<String, Double>();
    distinctAttributeValuesAmount.forEach(x -> {
      expectedResultMap.put(x, entities.stream()
          .filter(y -> y.getFieldAccessor(attributeToGroupBy).equals(x))
        .mapToDouble(t -> Double.parseDouble(t.getDuration())).average().getAsDouble());
    });

   DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");
    var expected = expectedResultMap.entrySet().stream()
        .map(x -> "Average for " + x.getKey() + " = " + DECIMAL_FORMAT.format(x.getValue()))
        .collect(Collectors.toList());

    var actual = processorUnit.process(entities, queryEntity);

    assertEquals(expected.size(), actual.size());
    assertTrue(actual.containsAll(expected));
  }

  private List<IMDbMovieEntity> prepareEntitiesList() throws URISyntaxException, FileHandlingException {
      return parser.parseFile(Path.of(ClassLoader.getSystemResource("validFile.csv").toURI()));
  }
}
