package com.illia.client.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class FileUtils {

    public String deleteFileIfExists(Path path) {
        try{
            Files.deleteIfExists(path);
            return "Deleted " + path;
        }catch (IOException e){
            log.error(e.getMessage());
            throw new FileHandlingError("Internal error while deleting file!");
        }
    }

    public String saveFile(Path filePath, byte[] content){
        try (var os = new FileOutputStream(filePath.toFile());
             var is = new ByteArrayInputStream(content)) {
            is.transferTo(os);
            os.flush();
            return "Saved file path: " + filePath;
        }catch (IOException e){
            log.error(e.getMessage());
            throw new FileHandlingError("Internal error while saving file!");
        }
    }

    public ByteArrayResource resolveMultipartFile(MultipartFile multipartFile) {
        try{
            return new ByteArrayResource(multipartFile.getBytes());
        }catch (IOException e){
            log.error(e.getMessage());
            throw new FileHandlingError("Internal error while resolving multipart file!");
        }
    }

    public boolean exists(Path path) {
        return Files.exists(path);
    }
}
