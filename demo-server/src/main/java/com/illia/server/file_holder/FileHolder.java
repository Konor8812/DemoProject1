package com.illia.server.file_holder;

import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

public interface FileHolder {

    byte[] getFile(String fileName) throws IOException;
    boolean saveFile(String fileName, ByteArrayResource byteArrayResource) throws IOException;
    Integer getFilesAmount();
    boolean exists(String fileName);
}
