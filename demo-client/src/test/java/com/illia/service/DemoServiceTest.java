package com.illia.service;

import com.illia.client.config.ClientConfig;
import com.illia.client.http_client.MyHttpClient;
import com.illia.client.service.DemoService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = {
        "test.savedFilesDirectory=testSavedFilesDirectory/",
})
@SpringBootTest(classes = {DemoService.class})
public class DemoServiceTest {

    @MockBean
    MyHttpClient client;
    @MockBean
    ClientConfig config;

    @Autowired
    DemoService service;

    @Value(value = "${test.savedFilesDirectory}")
    private String savedFilesDirectoryPrefix;

    private Path savedFilesDirectoryPath;


    @Test
    public void uploadFileShouldBeOk() throws IOException {
        when(client.performUploadFileRequest(any(), any()))
                .thenReturn(ResponseEntity.ok().build());

        var fileName = "fileName";
        try {
            var mockMultipartFile = new MockMultipartFile("fileName", "Content".getBytes());
            var response = service.uploadFile(fileName, mockMultipartFile);
            verify(client, atLeast(1)).performUploadFileRequest(any(), any());
            assertTrue(response.getStatusCode().is2xxSuccessful());
        } finally {
            Files.delete(Path.of(fileName));
        }
    }

    @Test
    public void uploadFileShouldBeBadRequest() {
        when(client.performUploadFileRequest(any(), any()))
                .thenReturn(ResponseEntity.ok().build());
        var response = service.uploadFile("fileName", null);
        verify(client, never()).performUploadFileRequest(any(), any());
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void downloadFileShouldBeOk() throws URISyntaxException, IOException {
        var fileName = "provided.csv";

        var preparedFilePathAsString = initPreparedFile(fileName);
        when(client.performDownloadFileRequest("baseUrl/downloadFile?fileName=existingFile"))
                .thenReturn(ResponseEntity.ok().body(preparedFilePathAsString));

        var response = service.downloadFile("existingFile");
        verify(client, atLeast(1)).performDownloadFileRequest("baseUrl/downloadFile?fileName=existingFile");
        assertTrue(response.getStatusCode().is2xxSuccessful());

        var body = (String) response.getBody();
        assertTrue(body.contains("Successfully downloaded"));
        assertTrue(Files.exists(Path.of(preparedFilePathAsString + "(" + "1" + ")")));
    }

    @Test
    public void downloadFileShouldBeBadRequest() {
        when(client.performDownloadFileRequest("baseUrl/downloadFile?fileName=nonExistingFile"))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        var fileName = "nonExistingFile";
        var response = service.downloadFile(fileName);
        verify(client, atLeast(1)).performDownloadFileRequest("baseUrl/downloadFile?fileName=nonExistingFile");
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @BeforeEach
    public void prepareMocks() {
        when(config.getBaseUrl()).thenReturn("baseUrl%s%s");
        when(config.getDownloadedFilesDirectoryPrefix()).thenReturn(savedFilesDirectoryPrefix);
    }

    private String initPreparedFile(String fileName) throws URISyntaxException, IOException {
        var uri = ClassLoader.getSystemResource(fileName).toURI();
        return Files.copy(Path.of(uri), Path.of(savedFilesDirectoryPrefix, fileName)).toString();
    }

    @BeforeEach
    public void clearAndCreateDirectory() throws IOException {
        savedFilesDirectoryPath = Path.of(savedFilesDirectoryPrefix);
        FileUtils.deleteDirectory(savedFilesDirectoryPath.toFile());
        Files.createDirectory(savedFilesDirectoryPath);
    }

    @AfterEach
    public void clearDirectory() throws IOException {
        FileUtils.deleteDirectory(savedFilesDirectoryPath.toFile());
    }
}
