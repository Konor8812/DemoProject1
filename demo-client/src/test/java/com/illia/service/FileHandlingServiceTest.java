package com.illia.service;


import com.illia.client.config.ClientConfig;

import com.illia.client.service.FileHandlingService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(classes = {FileHandlingService.class})
public class FileHandlingServiceTest {
    @MockBean
    RestTemplate restTemplate;

    @MockBean
    ClientConfig config;
    @Autowired
    FileHandlingService fileHandlingService;

    @Value(value = "${savedFilesDirectory}")
    private String savedFilesDirectoryPrefix;



    @Test
    public void resolveMultipartFileTest() throws IOException {
        var content = "Content".getBytes();
        var mockMultipartFile = new MockMultipartFile("multipartFile", content);

        var expected = new ByteArrayResource(content);
        var actual = fileHandlingService.resolveMultipartFile(mockMultipartFile);

        assertEquals(expected, actual);
    }

    @Test
    public void testSaveFile() throws IOException {
        when(config.getDownloadedFilesDirectoryPrefix())
                .thenReturn(savedFilesDirectoryPrefix);
        var fileName = "fileName";
        var content = "Content".getBytes();
        var savedFilesDirectoryPath = Path.of(savedFilesDirectoryPrefix);

        try {
            Files.createDirectory(savedFilesDirectoryPath);

            var response = fileHandlingService.saveFile(fileName, content, true);
            assertTrue(response);
            var savedFilePath = Path.of(config.getDownloadedFilesDirectoryPrefix(), fileName);
            assertTrue(Files.exists(savedFilePath));
            assertArrayEquals(content, Files.readAllBytes(savedFilePath));
        }finally {
            FileUtils.deleteDirectory(savedFilesDirectoryPath.toFile());
        }
    }

}
