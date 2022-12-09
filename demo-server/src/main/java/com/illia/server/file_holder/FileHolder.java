package com.illia.server.file_holder;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileHolder {

    File getFile(String fileName);
    String saveFile(String fileName, MultipartFile file);
    String saveFile(String fileName, File file);
    Integer getFilesAmount();
    void setPrefix(String prefix);
}
