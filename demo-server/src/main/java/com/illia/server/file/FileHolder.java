package com.illia.server.file;

import java.io.IOException;
import org.springframework.core.io.ByteArrayResource;

public interface FileHolder {

  byte[] getFile(String fileName) throws IOException;

  boolean saveFile(String fileName, ByteArrayResource byteArrayResource) throws IOException;

  Integer getFilesAmount();

  boolean exists(String fileName);
}
