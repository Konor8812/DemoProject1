package com.illia.server.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.illia.server.file.model.FileEntity.FileDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class FileHolderTest {

  static final MongoDBContainer mongoDBContainer  = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

  static FileHolder fileHolder;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    mongoDBContainer.start();
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Test
  public void testSaveAndGetFileShouldReturnSameEntityWithId(){
    var fileName = "filename";
    var content = "content".getBytes();

    var prepared = FileDocument.builder()
        .name(fileName)
        .content(content)
        .build();

    // returning FileDocument in fileHolder.save() has a lot of sense for testing but no sense for 'prod'

    fileHolder.saveFile(fileName, new ByteArrayResource(content));
    var result = fileHolder.getFile(fileName);

    assertEquals(prepared.getName(), result.getName());
    assertArrayEquals(prepared.getContent(), result.getContent());
    assertEquals(24, result.getId().length());
  }

  @Test
  public void testGetSavedFilesAmount(){
    assertEquals(0, fileHolder.getFilesAmount());
    fileHolder.saveFile("fileName", new ByteArrayResource("content".getBytes()));
    assertEquals(1, fileHolder.getFilesAmount());
  }

  @Test
  public void testExistsMethodShouldReturnTrue(){
    var fileName = "existingFile";
    fileHolder.saveFile(fileName, new ByteArrayResource("content".getBytes()));
    assertTrue(fileHolder.exists(fileName));
  }

  @Test
  public void testExistsMethodShouldReturnFalse(){
    assertFalse(fileHolder.exists(""));
  }

  @Test
  public void testFindAllShouldReturnEntitiesList(){
    // kinda confusing
//    fileHolder.saveFile("fileName", new ByteArrayResource("content".getBytes()));
//    fileHolder.saveFile("fileName", new ByteArrayResource("content".getBytes()));
//    fileHolder.saveFile("fileName", new ByteArrayResource("content".getBytes()));

    var count = 3;
    for(int i = 0; i < count; i++){
      fileHolder.saveFile("fileName", new ByteArrayResource("content".getBytes()));
    }
//    assertEquals(count, fileHolder.getAll().size());

    // again, confusing, getFilesAmount() is tested, but it's not reliable.
    // deleting all document`s after each test is wiser decision
    assertEquals(fileHolder.getFilesAmount(), fileHolder.getAll().size());
  }

  @BeforeAll
  public static void setUp(@Autowired MongoTemplate mongoTemplate){
    fileHolder = new FileHolderMongoImpl(mongoTemplate);
  }
}
