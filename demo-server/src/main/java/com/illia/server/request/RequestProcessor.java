package com.illia.server.request;

import com.illia.server.file.FileHolder;
import com.illia.server.file.model.FileEntity.FileDocument;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RequestProcessor {

  @Autowired
  private FileHolder fileHolder;

  public FileDocument proceedDownloadFile(String fileName) throws RequestProcessorException{
    var fileDocument = fileHolder.getFile(fileName);
    if(fileDocument == null){
      throw new RequestProcessorException("No such file!");
    }
    return fileDocument;
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

  public String getFilesAmount() {
    return String.valueOf(fileHolder.getFilesAmount());
  }

  public String getAllSavedFiles(){
    return fileHolder.getAll().stream()
        .map(x -> String.format("File %s is %d bytes", x.getName(), x.getContent().length))
        .collect(Collectors.joining(System.lineSeparator()));
  }

}
