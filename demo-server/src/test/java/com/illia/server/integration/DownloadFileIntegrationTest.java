package com.illia.server.integration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.illia.server.file.model.FileEntity.FileDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

@AutoConfigureMockMvc
@SpringBootTest
public class DownloadFileIntegrationTest {

  static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

  @Autowired
  MockMvc mvc;

  @Autowired
  MongoTemplate mongoTemplate;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    mongoDBContainer.start();
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Test
  public void downloadExistingFileTestShouldReturnFileDocumentFromDB() throws Exception {
    var fileName = "existingFile";

    var prepared = FileDocument.builder()
        .name(fileName)
        .content("content".getBytes())
        .build();
    mongoTemplate.save(prepared, "Files");

    var response = mvc.perform(get(String.format("/demo/downloadFile?fileName=%s", fileName))
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    assertEquals(prepared, new ObjectMapper().readValue(response, FileDocument.class));
  }

  @Test
  public void downloadNonExistingFileTestShouldBeBadRequest() throws Exception {
    var fileName = "nonExistingFile";

    assertEquals("No such file!",
        mvc.perform(get(String.format("/demo/downloadFile?fileName=%s", fileName))
                .contentType("application/json"))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString());
  }
}
