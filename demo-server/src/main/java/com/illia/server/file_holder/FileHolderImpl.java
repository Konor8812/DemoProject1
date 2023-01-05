package com.illia.server.file_holder;

import com.illia.server.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FileHolderImpl implements FileHolder {

    @Autowired
    private ServerConfig serverConfig;


    private final Map<String, Path> savedFiles = new HashMap<>();

    @Override
    public byte[] getFile(String fileName) throws IOException {
        try {
            var filePath = savedFiles.get(fileName);
            if (filePath != null) {
                return Files.readAllBytes(filePath);
            } else {
                return null;
            }
        } catch (Exception ex) {
            log.error("Server error during 'get file' operation!");
            throw ex;
        }
    }

    @Override
    public boolean saveFile(String fileName, ByteArrayResource byteArrayResource) throws IOException {
        if (isValid(byteArrayResource)) {
            try {
                var path = Path.of(serverConfig.getSavedFilesDirectory() + fileName);
                Files.deleteIfExists(path);

                try (var os = new FileOutputStream(path.toFile());
                     var is = byteArrayResource.getInputStream()) {
                    is.transferTo(os);
                    os.flush();
                }
                savedFiles.put(fileName, path);
                return true;

            } catch (Exception ex) {
                log.error("Server error during 'save file' operation!", ex);
                throw ex;
            }
        }
        return false;
    }

    private boolean isValid(ByteArrayResource byteArrayResource) {
        return byteArrayResource != null && byteArrayResource.contentLength() > 0;
    }


    @Override
    public Integer getFilesAmount() {
        try {
            return savedFiles.size();
        } catch (Exception ex) {
            log.error("Server error during 'get saved files amount' operation!");
            return -1;
        }
    }

    @Override
    public boolean exists(String fileName) {
        return savedFiles.get(fileName) != null;
    }

}
