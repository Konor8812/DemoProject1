package com.illia.server.file.model.FileEntity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Data
@Builder
public class FileDocument {

  @Id
  String id;

  @Field
  String name;

  @Field
  byte[] content;

}
