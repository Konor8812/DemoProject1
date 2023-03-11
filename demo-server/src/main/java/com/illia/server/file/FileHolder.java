package com.illia.server.file;

import com.illia.server.file.model.FileEntity.FileDocument;
import com.illia.server.request.RequestProcessorException;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;

public interface FileHolder {

  FileDocument getFile(String fileName) throws RequestProcessorException;

  FileDocument saveFile(String fileName, ByteArrayResource byteArrayResource) throws RequestProcessorException;

  long getFilesAmount();

  boolean exists(String fileName);

  List<FileDocument> getAll();
}
