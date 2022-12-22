package com.illia.server.file_holder;

import com.illia.server.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FileHolderImpl implements FileHolder {

    @Autowired
    ServerConfig serverConfig;


    private final Map<String, Path> savedFiles = new HashMap<>();

    @Override
    public byte[] getFile(String fileName) throws IOException {
        try {
            return Files.readAllBytes(savedFiles.get(fileName));
        } catch (Exception ex) {
            log.error("Server error during 'get file' operation!");
            throw ex;
        }
    }

    @Override
    public String saveFile(String fileName, File file) {
            try {
                var content = Files.readAllBytes(file.toPath());

                if (content.length > 0) {
                    var path = Path.of(serverConfig.getSavedFilesDirectory() + fileName);
                    var updatedExisting = Files.deleteIfExists(path);

                    try (var os = new FileOutputStream(path.toFile());
                         var is = new ByteArrayInputStream(content)) {
                        is.transferTo(os);
                        os.flush();
                    }
                    savedFiles.put(fileName, path);
                    if (updatedExisting) {
                        return "Updated existing file on server " + fileName;
                    } else {
                        return "Saved file on server " + fileName;
                    }
                }
                else {
                        return "File is null, nothing to save!";
                    }
            } catch (Exception ex) {
                var internalErrorMsg = "Server error during 'save file' operation!";
                log.error(internalErrorMsg, ex);
                return internalErrorMsg;
            }
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

}
