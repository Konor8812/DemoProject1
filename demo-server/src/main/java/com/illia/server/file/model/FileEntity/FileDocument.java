package com.illia.server.file.model.FileEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
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
