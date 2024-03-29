package com.illia.client.http;


import com.illia.client.config.ClientConfig;
import com.illia.client.model.file.FileEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
public class MyHttpClientImpl implements MyHttpClient {

  private static final String UPLOAD_FILE_BASE_URL = "/uploadFile?fileName=";
  private static final String DOWNLOAD_FILE_BASE_URL = "/downloadFile?fileName=";
  private static final String GET_ALL_SAVED_FILES_LIST = "/all";
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ClientConfig clientConfig;

  @Override
  public ResponseEntity<String> performUploadFileRequest(String fileName, ByteArrayResource resource, boolean overwrite) {
    var url = String.format(clientConfig.getBaseUrl(), UPLOAD_FILE_BASE_URL, fileName);

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    if (overwrite) {
      headers.add("Overwrite", "true");
    }

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("resource", resource);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    return restTemplate.postForEntity(url, requestEntity, String.class);

  }

  @Override
  public ResponseEntity<FileEntity> performDownloadFileRequest(String fileName) {
    var url = String.format(clientConfig.getBaseUrl(), DOWNLOAD_FILE_BASE_URL, fileName);
    return restTemplate.getForEntity(url, FileEntity.class);
  }

  @Override
  public ResponseEntity<String> getAllSavedFiles(){
    var url = String.format(clientConfig.getBaseUrl(), GET_ALL_SAVED_FILES_LIST, ""); //bp
    return restTemplate.getForEntity(url, String.class);
  }

}
