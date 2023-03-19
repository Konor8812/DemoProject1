package com.illia.server.integration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.illia.server.file.model.FileEntity.FileDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@AutoConfigureMockMvc
@SpringBootTest
public class UploadFileIntegrationTest {

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
  public void uploadValidFileTestShouldSaveToDatabaseAndReturnOk() throws Exception {
    var fileName = "fileName";
    var fileContent = "fileContent".getBytes();

    mvc.perform(multipart(String.format("/demo/uploadFile?fileName=%s", fileName))
            .part(new MockPart("resource", fileName, fileContent))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header("Overwrite", "true"))
        .andExpect(status().isOk())
        .andExpect(content().string(String.format("File %s saved successfully on server", fileName)));

    var filesFromDB = mongoTemplate.findAll(FileDocument.class, "Files");
    assertEquals(1, filesFromDB.size());

    var fileDocument = filesFromDB.get(0);
    assertEquals(fileName, fileDocument.getName());
    assertArrayEquals(fileContent, fileDocument.getContent());
  }

  @Test
  public void uploadFileWithInvalidNameTestShouldNotSaveToDatabaseAndReturnBadRequest() throws Exception {
    var fileName = "";
    var mockPart = new MockPart("resource", fileName, "fileContent".getBytes());

    mvc.perform(multipart(String.format("/demo/uploadFile?fileName=%s", fileName))
            .part(mockPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("File with such name can`t be stored!"));

    assertEquals(0, mongoTemplate.findAll(FileDocument.class, "Files").size());
    assertTrue(mongoTemplate.find(Query.query(Criteria
        .where("name")
        .is("")), FileDocument.class, "Files").isEmpty());
  }

  @Test
  public void uploadFileWithInvalidContentTestShouldNotSaveToDatabaseAndReturnBadRequest() throws Exception {
    var fileName = "fileName";
    var content = "".getBytes();
    var mockPart = new MockPart("resource", fileName, content);

    mvc.perform(multipart(String.format("/demo/uploadFile?fileName=%s", fileName))
            .part(mockPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Empty file can't be stored!"));

    assertEquals(0, mongoTemplate.findAll(FileDocument.class, "Files").size());

    assertTrue(mongoTemplate.find(Query.query(Criteria
        .where("name")
        .is(fileName)
        .and("content")
        .is(content)), FileDocument.class, "Files").isEmpty());
  }


  @Test
  public void uploadFileWithNameCollisionAndOverwriteFalseTestShouldNotSaveToDatabaseAndReturnBadRequest() throws Exception {
    var fileName = "fileName";
    var content = "fileContent".getBytes();
    mongoTemplate.save(FileDocument.builder()
        .name(fileName)
        .content(content)
        .build(), "Files");

    var mockPart = new MockPart("resource", fileName, "differentContentFromThatSavedInDb".getBytes());
    mvc.perform(multipart(String.format("/demo/uploadFile?fileName=%s", fileName))
            .part(mockPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header("Overwrite", "false"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("File with such name already stored on server. " +
            "Consider adding overwrite=true request header to overwrite existing file or change file name"));

    assertEquals(1, mongoTemplate.findAll(FileDocument.class, "Files").size());
    assertArrayEquals(content, mongoTemplate.find(Query.query(Criteria
                .where("name")
                .is(fileName)
                .and("content")
                .is(content)),
            FileDocument.class, "Files").get(0)
        .getContent());
  }

  @AfterEach
  public void cleanFilesCollection() {
    mongoTemplate.remove(new Query(), "Files");
  }

}
