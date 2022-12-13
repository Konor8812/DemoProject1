package com.illia.model;

import com.illia.client.model.IMDbMovieReport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTest {

    @Test
    public void parsedFileEntitiesAmountTest() throws URISyntaxException, IOException {
        var uri = ClassLoader.getSystemResource("shortProvided.csv").toURI();
        var filePath = Path.of(uri);

        var linesAmount = Files.readAllLines(filePath).size();
        var expectedEntitiesAmount = linesAmount - 1; // 1st row isn't entity

        var actualEntitiesAmount = IMDbMovieReport.parse(filePath.toFile()).getEntitiesAmount();
        assertEquals(expectedEntitiesAmount, actualEntitiesAmount);
    }

}
