package com.illia.server.file;

import com.illia.server.file.model.FileEntity.FileDocument;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


@Service
public class FileHolderMongoImpl implements FileHolder {

  MongoTemplate mongoTemplate;

  @Override
  public FileDocument getFile(String fileName) {
    return mongoTemplate
        .findOne(Query.query(Criteria
                .where("name")
                .is(fileName)),
            FileDocument.class,
            "Files");
  }

  @Override
  public void saveFile(String fileName, ByteArrayResource byteArrayResource) {
    mongoTemplate.findAndReplace(Query.query(Criteria
            .where("name")
            .is(fileName)),
        FileDocument.builder()
            .content(byteArrayResource.getByteArray())
            .name(fileName)
            .build(),
        FindAndReplaceOptions.options().upsert(),
        "Files");
  }

  @Override
  public long getFilesAmount() {
    return mongoTemplate.getCollection("Files").countDocuments();
  }

  @Override
  public boolean exists(String fileName) {
    return mongoTemplate
        .exists(Query.query(Criteria
                .where("name")
                .is(fileName)),
            "Files");
  }

  @Override
  public List<FileDocument> getAll() {
    return mongoTemplate.findAll(FileDocument.class, "Files");
  }


  public FileHolderMongoImpl(@Autowired MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

}
