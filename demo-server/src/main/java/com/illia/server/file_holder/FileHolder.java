package com.illia.server.file_holder;


import java.io.File;

public interface FileHolder {

    File getFile(String fileName);
    File saveFile(String fileName, File file);

}
