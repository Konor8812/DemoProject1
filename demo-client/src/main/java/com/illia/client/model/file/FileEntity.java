package com.illia.client.model.file;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileEntity {
  String id;

  String name;

  byte[] content;
}
