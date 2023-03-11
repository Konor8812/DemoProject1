package com.illia.server.request;

import com.illia.server.file.FileHolderMongoImpl;
import com.illia.server.file.model.FileEntity.FileDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RequestProcessor {

  @Autowired
  private FileHolderMongoImpl fileHolder;

  public FileDocument proceedDownloadFile(String fileName) throws RequestProcessorException {
    return fileHolder.getFile(fileName);
  }


  public String proceedSaveFile(String fileName, ByteArrayResource byteArrayResource, boolean overwrite) throws RequestProcessorException {
    if (!overwrite && fileHolder.exists(fileName)) {
     throw new RequestProcessorException("File with such name already stored on server. " +
          "Consider adding overwrite=true request header to overwrite existing file or change file name");
    }
    return new String(fileHolder.saveFile(fileName, byteArrayResource).getContent());
  }


  public String getAllSavedFiles(){
    StringBuilder responseBuilder = new StringBuilder();
    fileHolder.getAll()
        .forEach(x -> responseBuilder
            .append(x.getName())
            .append(" | ")
            .append(x.getContent().length)
            .append(System.lineSeparator()));
    return responseBuilder.toString();
  }

  public long getFilesAmount() {
    return fileHolder.getFilesAmount();
  }


}
