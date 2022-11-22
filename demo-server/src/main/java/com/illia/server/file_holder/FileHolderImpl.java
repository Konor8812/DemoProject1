package com.illia.server.file_holder;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Component
public class FileHolderImpl implements FileHolder {

    private final Map<String, File> savedFiles = new HashMap<>();

    @Override
    public File getFile(String fileName) {
        return savedFiles.get(fileName);
    }

    @Override
    public File saveFile(String fileName, File file) {
        savedFiles.put(fileName, file);
        return file;
    }

    @Override
    public int getFilesAmount() {
        return savedFiles.size();
    }

}
