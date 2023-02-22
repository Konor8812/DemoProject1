package com.illia.client.service.processor.unit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.holder.IMDbMovieHolderImpl;
import com.illia.client.model.parser.IMDbMovieParser;
import com.illia.client.model.request.entity.CountQueryEntity;
import com.illia.client.model.request.registry.AttributeRegistry;
import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.query.processor.unit.CountOperationProcessorUnit;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {IMDbMovieParser.class, IMDbMovieHolderImpl.class, CountOperationProcessorUnit.class})
public class CountOperationProcessorUnitTest {

  @Autowired
  IMDbMovieParser parser;

  @Autowired
  CountOperationProcessorUnit processorUnit;

  @ParameterizedTest
  @ValueSource(strings = {"COUNTRY", "GENRE"})
  public void testShouldCountEntitiesForAttribute(AttributeRegistry attribute) throws FileHandlingException, URISyntaxException {

    var entities = prepareEntitiesList();
    var queryEntity = CountQueryEntity.builder()
        .attribute(attribute)
        .build();

    var distinctAttributeValuesAmount = entities.stream()
        .map(a -> a.getFieldAccessor(attribute))
        .distinct()
        .collect(Collectors.toList());

    var expectedResultMap = new HashMap<String, Long>();
    distinctAttributeValuesAmount.forEach(x -> {
      expectedResultMap.put(x, entities.stream()
          .filter(y -> y.getFieldAccessor(attribute).equals(x))
          .count());
    });

    var expected = expectedResultMap.entrySet().stream()
        .map(x -> x.getKey() + ": " + x.getValue())
        .collect(Collectors.toList());

    var actual = processorUnit.process(entities, queryEntity);

    assertEquals(expected.size(), actual.size());
    assertTrue(actual.containsAll(expected));
  }

  private List<IMDbMovieEntity> prepareEntitiesList() throws URISyntaxException, FileHandlingException {
    return parser.parseFile(Path.of(ClassLoader.getSystemResource("validFile.csv").toURI()));
  }
}
