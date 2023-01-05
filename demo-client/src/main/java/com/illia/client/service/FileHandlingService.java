package com.illia.client.service;

import com.illia.client.config.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public class FileHandlingService {

    @Autowired
    private ClientConfig clientConfig;

    public ByteArrayResource resolveMultipartFile(MultipartFile multipartFile) throws IOException {
        return new ByteArrayResource(multipartFile.getBytes());
    }

    public boolean saveFile(String fileName, byte[] content, boolean overwrite) throws IOException {
        var filePath = resolvePath(fileName);

        if (overwrite) {
            Files.deleteIfExists(filePath);
        }

        try (var os = new FileOutputStream(filePath.toFile());
             var is = new ByteArrayInputStream(content)) {
            is.transferTo(os);
            os.flush();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean deleteFile(String fileName) throws IOException {
        return Files.deleteIfExists(resolvePath(fileName));
    }

    public Path resolveFilePath(String fileName) {
        if (fileName != null && Files.exists(resolvePath(fileName))) {
            return resolvePath(fileName);
        }
        return null;
    }

    public boolean exists(String fileName) {
        return fileName != null && Files.exists(resolvePath(fileName));
    }

    private Path resolvePath(String... args) {
        var pathBuilder = new StringBuilder();
        pathBuilder.append(clientConfig.getDownloadedFilesDirectoryPrefix())
                .append("/");
        Stream.of(args).forEach(x -> pathBuilder.append(x).append("/"));
        return Path.of(pathBuilder.toString());
    }
}
