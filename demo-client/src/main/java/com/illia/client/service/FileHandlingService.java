package com.illia.client.service;

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
public class FileHandlingService {

    public boolean deleteFile(Path path) throws IOException {
        return Files.deleteIfExists(path);
    }

    public boolean saveFile(Path filePath, byte[] content){
        try (var os = new FileOutputStream(filePath.toFile());
             var is = new ByteArrayInputStream(content)) {
            is.transferTo(os);
            os.flush();
            return true;
        }catch (Exception e){
            log.error("Error occurred during saving file ", e);
            return false;
        }
    }

    public ByteArrayResource resolveMultipartFile(MultipartFile multipartFile) throws IOException {
        return new ByteArrayResource(multipartFile.getBytes());
    }
}
