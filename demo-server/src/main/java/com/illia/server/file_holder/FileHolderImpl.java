package com.illia.server.file_holder;

import com.illia.server.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FileHolderImpl implements FileHolder {

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    FileHandler fileHandler;

    private final Map<String, Path> savedFiles = new HashMap<>();

    @Override
    public byte[] getFile(String fileName) throws IOException {
        var filePath = savedFiles.get(fileName);
        if (filePath != null) {
            return fileHandler.getFileContent(filePath);
        } else {
            return null;
        }
    }

    @Override
    public boolean saveFile(String fileName, ByteArrayResource byteArrayResource) throws IOException {
        if (fileHandler.validateResource(byteArrayResource)) {
            var path = resolvePath(fileName);
            var saved = fileHandler.saveFile(path, byteArrayResource);
            if(saved) {
                savedFiles.put(fileName, path);
            }
            return saved;
        }
        return false;
    }


    @Override
    public Integer getFilesAmount() {
        return savedFiles.size();
    }

    @Override
    public boolean exists(String fileName) {
        return savedFiles.containsKey(fileName) && fileHandler.exists(resolvePath(fileName));
    }

    private Path resolvePath(String... args){
        return Path.of(serverConfig.getSavedFilesDirectory(), String.join("/", args));
    }
}
