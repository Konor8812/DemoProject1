package com.illia.client.http;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.illia.client.config.ClientConfig;
import com.illia.client.model.file.FileEntity;
import com.illia.client.service.file.FileHandlingException;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@SpringBootTest(classes = MyHttpClientImpl.class)
public class HttpClientTest {

  @MockBean
  RestTemplate restTemplate;

  @MockBean
  ClientConfig clientConfig;
  @Autowired
  MyHttpClient client;

  @Captor
  ArgumentCaptor<HttpEntity<MultiValueMap<String, Object>>> httpEntityArgumentCaptor;

  @Test
  public void uploadFileRequestEntityCreationTest() throws FileHandlingException {
    var fileName = "fileName";
    var expectedUrl = String.format(clientConfig.getBaseUrl(), "/uploadFile?fileName=", fileName);
    var overwriteFlag = true;
    var mockedResource = mock(ByteArrayResource.class);
    when(restTemplate.postForEntity(eq(expectedUrl), any(), any()))
        .thenReturn(ResponseEntity.ok().build());
    client.performUploadFileRequest("fileName", mockedResource, overwriteFlag);
    verify(restTemplate, times(1))
        .postForEntity(eq(expectedUrl), httpEntityArgumentCaptor.capture(), eq(String.class));

    var capturedEntity = httpEntityArgumentCaptor.getValue();
    var capturedResource = Objects.requireNonNull(capturedEntity.getBody()).getFirst("resource");
    assertEquals(mockedResource, capturedResource);

    var capturedHeaders = capturedEntity.getHeaders();
    var capturedOverwriteFlag = Boolean.parseBoolean(Objects.requireNonNull(capturedHeaders.get("overwrite")).get(0));
    assertTrue(capturedOverwriteFlag);
  }

  @Test
  public void uploadFileShouldHandle2xxSuccess() throws FileHandlingException {
    when(restTemplate.postForEntity(isA(String.class), any(), any()))
        .thenReturn(ResponseEntity.ok().build());

    assertTrue(client.performUploadFileRequest("fileName", mock(ByteArrayResource.class), false)
        .getStatusCode().is2xxSuccessful());
  }

  @Test
  public void downloadFileShouldCallRestTemplate() throws FileHandlingException {
    var fileName = "fileName";
    var expectedUrl = String.format(clientConfig.getBaseUrl(), "/downloadFile?fileName=", fileName);
    client.performDownloadFileRequest(fileName);
    verify(restTemplate, times(1))
        .getForEntity(eq(expectedUrl), eq(FileEntity.class));
  }

  @Test
  public void downloadFileShouldHandle2xxOK() throws FileHandlingException {
    when(restTemplate.getForEntity(isA(String.class), any()))
        .thenReturn(ResponseEntity.ok().build());
    assertTrue(client.performDownloadFileRequest("").getStatusCode().is2xxSuccessful());
  }

  @BeforeEach
  public void prepareMock() {
    when(clientConfig.getBaseUrl()).thenReturn("url%s%s");
  }

}
