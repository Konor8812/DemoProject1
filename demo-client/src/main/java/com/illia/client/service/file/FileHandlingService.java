package com.illia.client.service.file;

import com.illia.client.config.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class FileHandlingService {

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    FileUtil fileUtil;

    public ByteArrayResource resolveMultipartFile(MultipartFile multipartFile) throws IOException {
        return fileUtil.resolveMultipartFile(multipartFile);
    }

    public boolean saveFile(String fileName, byte[] content, boolean overwrite) throws IOException {
        var filePath = resolvePath(fileName);
        if (overwrite) {
            fileUtil.deleteFileIfExists(filePath);
        }
        return fileUtil.saveFile(filePath, content);
    }

    public boolean deleteFile(String fileName) throws IOException {
        var filePath = resolveFilePath(fileName);
        if(filePath != null){
            return fileUtil.deleteFileIfExists(filePath);
        }
        return false;
    }

    public Path resolveFilePath(String fileName) {
        Path filePath;
        if (fileName != null && fileUtil.exists((filePath = resolvePath(fileName)))) {
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
