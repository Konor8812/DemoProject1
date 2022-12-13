package com.illia.client;


import com.illia.client.config.ClientConfig;
import com.illia.client.http_client.MyHttpClient;
import com.illia.client.http_client.MyHttpClientImpl;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MyHttpClientImpl.class)
public class HttpClientTest {

    @MockBean
    ClientConfig clientConfig;

    @MockBean
    RestTemplate restTemplate;

    @Autowired
    MyHttpClient client;

    private final String prefix = "test_client_saved_file_folder/";

    @Test
    public void uploadFileShouldBeOk(){
        when(restTemplate.postForEntity((String) any(), any(), any())).thenReturn(ResponseEntity.ok().body("Uploaded"));

        var response = client.uploadFile("fileName", null);
        assertEquals("Uploaded", response.getBody());
        verify(clientConfig, atLeastOnce()).getBaseUrl();
        verify(restTemplate, atLeastOnce()).postForEntity((String) any(), any(), any());
    }

    @Test
    public void downloadFileShouldBeOk() throws URISyntaxException, IOException {
        var fileName = "provided.csv";

        var uri = ClassLoader.getSystemResource(fileName).toURI();
        Files.copy(Path.of(uri), Path.of(prefix, fileName));

        var response = client.downloadFile("existingFile");

        assertTrue(response.getStatusCode().is2xxSuccessful());

        var downloadedFilePath = Path.of(prefix, fileName+ "(" + "1" + ")");
        assertTrue(Files.exists(downloadedFilePath));
    }

    @Test
    public void downloadFileShouldBeBadRequest(){
        var response = client.downloadFile("nonExistingFile");
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @BeforeEach
    public void clearAndCreateDirectory() throws IOException {
        var directoryPath = Path.of(prefix);
        client.setPrefix(prefix);
        FileUtils.deleteDirectory(directoryPath.toFile());
        Files.createDirectory(directoryPath);
    }

    @AfterEach
    public void clearDirectory() throws IOException {
        var directoryPath = Path.of(prefix);
        FileUtils.deleteDirectory(directoryPath.toFile());
    }

    @BeforeEach
    public void prepareMocks(){
        when(restTemplate.getForEntity("baseUrl/downloadFile?fileName=existingFile", Object.class)).thenReturn(ResponseEntity.ok().body(prefix + "provided.csv"));
        when(restTemplate.getForEntity("baseUrl/downloadFile?fileName=nonExistingFile", Object.class)).thenReturn(ResponseEntity.badRequest().body("Non existing file"));
        when(clientConfig.getBaseUrl()).thenReturn("baseUrl%s%s");
    }
}
