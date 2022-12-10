package com.illia.server.file_holder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    private final Map<String, File> savedFiles = new HashMap<>();
    private String prefix = "demo-server/savedFiles/";

    @Override
    public File getFile(String fileName) {
        try {
            return savedFiles.get(fileName);
        } catch (Exception ex) {
            log.error("Server error during 'get file' operation!");
            throw ex;
        }
    }

    @Override
    public String saveFile(String fileName, MultipartFile mpFile) {
        try {
            var path = Path.of(prefix + fileName);
            var updatedExisting = Files.deleteIfExists(path);
            Files.createFile(path);
            mpFile.transferTo(path);
            savedFiles.put(fileName, path.toFile());

            if (updatedExisting) {
                return "Updated existing file " + fileName;
            } else {
                return "Saved file " + fileName;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            var internalErrorMsg = "Server error during 'save file' operation!";
            log.error(internalErrorMsg);
            return internalErrorMsg;
        }
    }

    @Override
    public String saveFile(String fileName, File file) {
        try {
            var path = Path.of(prefix + fileName);
            var updatedExisting = Files.deleteIfExists(path);
            Files.move(file.toPath(), path);
            savedFiles.put(fileName, path.toFile());

            if (updatedExisting) {
                return "Updated existing file " + fileName;
            } else {
                return "Saved file on server " + fileName;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            var internalErrorMsg = "Server error during 'save file' operation!";
            log.error(internalErrorMsg);
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

    @Override
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
}
