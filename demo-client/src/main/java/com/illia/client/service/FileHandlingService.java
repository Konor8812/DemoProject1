package com.illia.client.service;

import com.illia.client.config.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileHandlingService {

    @Autowired
    ClientConfig clientConfig;

    // needed?
    public File resolveMultipartFile(String fileName, MultipartFile multipartFile) throws IOException {

        File file = null;
        if (multipartFile != null) {
            file = new File(fileName);
            try (var is = multipartFile.getInputStream();
                 var os = new FileOutputStream(file)) {
                is.transferTo(os);
                os.flush();
            }
        }
        return file;
    }

    public boolean saveFile(String fileName, byte[] content) throws IOException{
        var filePath = Path.of(clientConfig.getDownloadedFilesDirectoryPrefix(), fileName);
        var fileAlreadyExists = Files.deleteIfExists(filePath);

        try(var os = new FileOutputStream(filePath.toFile());
            var is = new ByteArrayInputStream(content)){
            is.transferTo(os);
            os.flush();
        }
        return !fileAlreadyExists;
    }

}
