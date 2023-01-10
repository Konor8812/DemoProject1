package com.illia.client.service;

import com.illia.client.config.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public class FileHandlingProxyService {

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    FileHandlingService fileHandlingService;

    public ByteArrayResource resolveMultipartFile(MultipartFile multipartFile) throws IOException {
        return fileHandlingService.resolveMultipartFile(multipartFile);
    }

    public boolean saveFile(String fileName, byte[] content, boolean overwrite) throws IOException {
        var filePath = resolvePath(fileName);
        if (overwrite) {
            fileHandlingService.deleteFile(filePath);
        }
        return fileHandlingService.saveFile(filePath, content);
    }

    public boolean deleteFile(String fileName) throws IOException {
        var filePath = resolveFilePath(fileName);
        if(filePath != null){
            return fileHandlingService.deleteFile(filePath);
        }
        return false;
    }

    public Path resolveFilePath(String fileName) {
        Path filePath;
        if (fileName != null && Files.exists((filePath = resolvePath(fileName)))) {
            return filePath;
        }
        return null;
    }

    public boolean exists(String fileName) {
        return resolveFilePath(fileName) != null;
    }

    private Path resolvePath(String... args) {
        return Path.of(clientConfig.getDownloadedFilesDirectoryPrefix() + String.join("/", args));
    }
}
