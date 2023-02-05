package com.illia.client.service.file;

import com.illia.client.config.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class FileHandlingService {

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private FileUtils fileUtils;

    public ByteArrayResource resolveMultipartFile(MultipartFile multipartFile) {
        return fileUtils.resolveMultipartFile(multipartFile);
    }

    public String saveFile(String fileName, byte[] content, boolean overwrite) {
        var filePath = resolvePath(fileName);
        if (overwrite) {
            fileUtils.deleteFileIfExists(filePath);
        }
        return fileUtils.saveFile(filePath, content);
    }

    public String deleteFile(String fileName) throws FileHandlingException {
        return fileUtils.deleteFileIfExists(resolveFilePath(fileName));
    }

    public Path resolveFilePath(String fileName) throws FileHandlingException {
        Path filePath;
        if (fileName != null && fileUtils.exists((filePath = resolvePath(fileName)))) {
            return filePath;
        }
        throw new FileHandlingException(String.format("File '%s' isn't found", fileName));
    }

    public boolean exists(String fileName) {
        return fileUtils.exists(resolvePath(fileName));
    }

    private Path resolvePath(String... args) {
        return Path.of(clientConfig.getDownloadedFilesDirectoryPrefix() + String.join("/", args));
    }
}
