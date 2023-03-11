package com.illia.server.file;

import com.illia.server.file.model.FileEntity.FileDocument;
import com.illia.server.request.RequestProcessorException;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


@Service
public class FileHolderMongoImpl implements FileHolder {

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public FileDocument getFile(String fileName) throws RequestProcessorException {
    try{
      return Objects.requireNonNull(mongoTemplate  // Objects.require not null vs SuppressWarnings
          .findOne(Query.query(Criteria
              .where("name")
              .is(fileName)),
              FileDocument.class,
              "Files"));
    } catch (Exception ex){
      throw new RequestProcessorException("No such file " + fileName);
    }
  }

  @Override
  public FileDocument saveFile(String fileName, ByteArrayResource byteArrayResource) throws RequestProcessorException {
    if(byteArrayResource == null || byteArrayResource.contentLength() == 0){
      throw new RequestProcessorException("File is either empty or absent, nothing to store");
    }

    return  mongoTemplate.save(FileDocument.builder()
            .content(byteArrayResource.getByteArray())
            .name(fileName)
            .build(),
        "Files");
  }

  @Override
  public long getFilesAmount() {
    return mongoTemplate.getCollection("Files").countDocuments();
  }

  @Override
  public boolean exists(String fileName) {
    return !mongoTemplate
        .exists(Query.query(Criteria
            .where("name")
            .is(fileName)),
        byte[].class,
        "Files");
  }

  @Override
  public List<FileDocument> getAll(){
    return mongoTemplate.findAll(FileDocument.class, "Files");
  }

}
