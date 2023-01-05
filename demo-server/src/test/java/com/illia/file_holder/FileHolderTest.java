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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.TestPropertySource;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
    public void testSaveFile() throws IOException {
        String fileName = "provided.csv";

        var file = new ByteArrayResource(fileName.getBytes());

        var response = fileHolder.saveFile(fileName, file);
        assertTrue(response);
    }

    @Test
    public void testSaveInvalidFile() throws IOException {
        var file = new ByteArrayResource(new byte[] {});
        var response = fileHolder.saveFile("fileName", file);
        assertFalse(response);
    }


    @Test
    public void testGetFileShouldBeOk() throws IOException {
        String fileName = "existingFile";
        var directoryPath = Path.of(savedFilesDirectory);
        try {
            when(serverConfig.getSavedFilesDirectory())
                    .thenReturn(savedFilesDirectory);
            Files.createDirectory(directoryPath);

            var expected = "Content".getBytes();
            var file = new ByteArrayResource(expected);

            fileHolder.saveFile(fileName, file);
            var actual = fileHolder.getFile(fileName);

            assertArrayEquals(expected, actual);
            assertTrue(Files.exists(directoryPath.resolve(fileName)));
        }finally {
            FileUtils.deleteDirectory(directoryPath.toFile());
        }
    }

    @Test
    public void testGetFileShouldBeNull() throws IOException {
        String fileName = "nonExistingFile";
        var actual = fileHolder.getFile(fileName);

        assertNull(actual);
    }



}
