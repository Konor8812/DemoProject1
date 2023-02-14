package com.illia.client.service.file;

import com.illia.client.http.MyHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileTransferService {

  @Autowired
  private MyHttpClient client;

  @Autowired
  private FileHandlingService fileHandlingService;

  public ResponseEntity<String> uploadFile(String fileName, MultipartFile multipartFile, boolean overwrite) throws FileHandlingException {
    return client.performUploadFileRequest(fileName, fileHandlingService.resolveMultipartFile(multipartFile), overwrite);

  }

  public String downloadFile(String fileName, boolean overwrite) throws FileHandlingException {
    if (!overwrite) {
      if (fileHandlingService.exists(fileName)) {
        throw new FileHandlingException("File with such name already exists!");
      }
    }
    return fileHandlingService.saveFile(fileName, client.performDownloadFileRequest(fileName).getBody(), true);
  }

}
