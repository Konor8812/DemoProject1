package com.illia.server.file_holder;

import java.io.File;
import java.io.IOException;

public interface FileHolder {

    byte[] getFile(String fileName) throws IOException;
    String saveFile(String fileName, File file);
    Integer getFilesAmount();
}
