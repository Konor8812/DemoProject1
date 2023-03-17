package com.illia.server.request;

import com.illia.server.file.FileHolder;
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
  private FileHolder fileHolder;

  public FileDocument proceedDownloadFile(String fileName) {
    return fileHolder.getFile(fileName);
  }

  public String proceedSaveFile(String fileName, ByteArrayResource byteArrayResource, boolean overwrite) throws RequestProcessorException {
    if(fileName == null || fileName.isBlank()){
      throw new RequestProcessorException("File with such name can`t be stored!");
    }else if(byteArrayResource == null || byteArrayResource.contentLength() == 0){
      throw new RequestProcessorException("Empty file can't be stored!");
    }else if (!overwrite && fileHolder.exists(fileName)) {
     throw new RequestProcessorException("File with such name already stored on server. " +
          "Consider adding overwrite=true request header to overwrite existing file or change file name");
    }
    fileHolder.saveFile(fileName, byteArrayResource);
    return String.format("File %s saved successfully on server", fileName);
  }

  public long getFilesAmount() {
    return fileHolder.getFilesAmount();
  }

}
