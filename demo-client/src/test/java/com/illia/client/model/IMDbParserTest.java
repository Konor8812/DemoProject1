package com.illia.client.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {IMDbMovieParser.class})
public class IMDbParserTest {
    @Autowired
    IMDbMovieParser parser;

    @MockBean
    IMDbMovieHolderImpl holder;

    @Test
    public void parseFileTestEntitiesAmount() throws URISyntaxException, IOException {
        var fileName = "provided.csv";
        var uri = ClassLoader.getSystemResource(fileName).toURI();
        var filePath = Path.of(uri);

        var linesAmount = Files.readAllLines(filePath).size();
        var expectedEntitiesAmount = linesAmount - 1; // 1st row isn't entity

        var parseResult = parser.parseFile(filePath.toFile());

        verify(holder, times(1)).saveEntities(eq(fileName), eq(parseResult));
        assertEquals(expectedEntitiesAmount, parseResult.size());
    }
}
