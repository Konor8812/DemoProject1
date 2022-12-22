package com.illia.file_holder;

import com.illia.server.config.ServerConfig;
import com.illia.server.file_holder.FileHolder;
import com.illia.server.file_holder.FileHolderImpl;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(classes = {FileHolderImpl.class})
public class FileHolderTest {

    @Autowired
    FileHolder fileHolder;
    @MockBean
    ServerConfig serverConfig;

    @Value("${savedFilesDirectory}")
    String savedFilesDirectory;

    @Test
    public void testSaveFile() throws URISyntaxException {
        String fileName = "provided.csv";

        var uri = ClassLoader.getSystemResource(fileName).toURI();
        var filePath = Path.of(uri);
        var file = filePath.toFile();

        var response = fileHolder.saveFile(fileName, file);
        assertEquals("Saved file on server provided.csv", response);

        response = fileHolder.saveFile(fileName, file);
        assertEquals("Updated existing file on server provided.csv", response);
    }

    @Test
    public void testGetFile() throws URISyntaxException, IOException {
        String fileName = "provided.csv";

        var uri = ClassLoader.getSystemResource(fileName).toURI();
        var filePath = Path.of(uri);
        var expected = Files.readAllBytes(filePath);

        fileHolder.saveFile(fileName, filePath.toFile());
        var actual = fileHolder.getFile(fileName);

        assertArrayEquals(expected, actual);
    }


    @BeforeEach
    public void clearAndCreateDirectory() throws IOException {
        when(serverConfig.getSavedFilesDirectory())
                .thenReturn(savedFilesDirectory);

        var directoryPath = Path.of(savedFilesDirectory);
        FileUtils.deleteDirectory(directoryPath.toFile());
        Files.createDirectory(directoryPath);
    }

    @AfterEach
    public void clearDirectory() throws IOException {
        var directoryPath = Path.of(savedFilesDirectory);
        FileUtils.deleteDirectory(directoryPath.toFile());
    }


}
