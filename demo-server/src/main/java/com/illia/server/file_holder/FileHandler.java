package com.illia.server.file_holder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class FileHandler {
    
    public byte[] getFileContent(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public boolean validateResource(ByteArrayResource byteArrayResource) {
        return byteArrayResource != null && byteArrayResource.contentLength() > 0;
    }

    public boolean saveFile(Path path, ByteArrayResource byteArrayResource) throws IOException {
        Files.deleteIfExists(path);

        try (var os = new FileOutputStream(path.toFile());
             var is = byteArrayResource.getInputStream()) {
            is.transferTo(os);
            os.flush();
        }

        return exists(path);
    }

    public boolean exists(Path path) {
        return Files.exists(path);
    }
}
