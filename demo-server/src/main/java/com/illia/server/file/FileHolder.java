package com.illia.server.file;

import com.illia.server.file.model.FileEntity.FileDocument;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;

public interface FileHolder {

  FileDocument getFile(String fileName) ;

  void saveFile(String fileName, ByteArrayResource byteArrayResource) ;

  long getFilesAmount();

  boolean exists(String fileName);

  List<FileDocument> getAll();
}
