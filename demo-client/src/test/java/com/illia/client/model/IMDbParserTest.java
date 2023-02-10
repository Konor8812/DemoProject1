package com.illia.client.model;

import com.illia.client.model.holder.IMDbMovieHolderImpl;
import com.illia.client.model.parser.IMDbMovieParser;
import com.illia.client.service.file.FileHandlingException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {IMDbMovieParser.class})
public class IMDbParserTest {
    @Autowired
    IMDbMovieParser parser;

    @MockBean
    IMDbMovieHolderImpl holder;

    @Captor
    ArgumentCaptor<List<IMDbMovieEntity>> holderArgumentCaptor;

    @Test
    public void parseValidFileShouldBeOkAndCallHolderSaveEntities() throws URISyntaxException, IOException, FileHandlingException {
        var fileName = "validFile.csv";
        var filePath = Path.of(ClassLoader.getSystemResource(fileName).toURI());
        parser.parseFile(filePath);

        verify(holder, times(1)).saveEntities(eq(fileName), holderArgumentCaptor.capture());

        assertEquals(Files.readAllLines(filePath).size() - 1, holderArgumentCaptor.getValue().size());
    }

    @Test
    public void parseUnreadableFileShouldThrowFileHandlingException() throws URISyntaxException {
        var fileName = "unparseable-file.csv";
        var filePath = Path.of(ClassLoader.getSystemResource(fileName).toURI());

        var parseResult = assertThrowsExactly(FileHandlingException.class,
                () -> parser.parseFile(filePath));

        verify(holder, never()).saveEntities(any(), any());
        assertEquals("File isn't readable!", parseResult.getMessage());
    }
}
