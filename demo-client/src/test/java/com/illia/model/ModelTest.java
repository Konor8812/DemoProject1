package com.illia.model;

import com.illia.client.model.IMDbMovieEntity;
import com.illia.client.model.IMDbMovieHolder;
import com.illia.client.model.IMDbMovieHolderImpl;
import com.illia.client.model.IMDbMovieParser;
import com.illia.client.service.processor_registry.processors.DeleteValuesOperationProcessor;
import com.illia.client.service.processor_registry.processors.SortOperationProcessor;
import com.illia.client.service.processor_registry.processors.SortOperationProcessor2;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {IMDbMovieParser.class, SortOperationProcessor2.class, DeleteValuesOperationProcessor.class})
public class ModelTest {

    @MockBean
    IMDbMovieHolderImpl holder;

    @Autowired
    IMDbMovieParser parser;

    @Test
    public void parsedFileEntitiesAmountTest() throws URISyntaxException, IOException {
        var uri = ClassLoader.getSystemResource("provided.csv").toURI();
        var filePath = Path.of(uri);

        var linesAmount = Files.readAllLines(filePath).size();
        var expectedEntitiesAmount = linesAmount - 1; // 1st row isn't entity

        var actualEntitiesAmount = parser.parseFile(filePath.toFile()).size();
        assertEquals(expectedEntitiesAmount, actualEntitiesAmount);
    }


    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException, IOException, URISyntaxException {
        var uri = ClassLoader.getSystemResource("provided.csv").toURI();
        var filePath = Path.of(uri);

        var entities = parser.parseFile(filePath.toFile());

        var operation = "date";
        var attribute = IMDbMovieEntity.class.getDeclaredField(operation);
        attribute.setAccessible(true);

        entities = entities.stream().filter(x -> {
            try {
                var result = (String) attribute.get(x);
                return result.contains("9");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toList());

        System.out.println(entities.size());
    }

    @Autowired
    private SortOperationProcessor2 sortOperationProcessor2;

    @Test
    public void testest() throws URISyntaxException, NoSuchFieldException {
        var uri = ClassLoader.getSystemResource("provided.csv").toURI();
        var filePath = Path.of(uri);

        var entities = parser.parseFile(filePath.toFile());

        var operation = "duration";
        var paramsMap = new HashMap<String, String>();
        paramsMap.put("attribute", operation);
        paramsMap.put("order", "asc");

        var result = sortOperationProcessor2.proceed(entities, paramsMap);
        result.forEach(System.out::println);
    }
}
