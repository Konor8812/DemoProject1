package com.illia.server.request;

import com.illia.server.file.FileHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RequestProcessorImpl implements RequestProcessor {

  @Autowired
  private FileHolder fileHolder;

  @Override
  public ResponseEntity<Object> proceedDownloadFile(String fileName) {
    try {
      var fileHolderResponse = fileHolder.getFile(fileName);
      if (fileHolderResponse != null) {
        return ResponseEntity.ok().body(fileHolderResponse);
      } else {
        return ResponseEntity.badRequest().body("No such file!".getBytes());
      }
    } catch (Exception ex) {
      var errorMsg = "Internal server error!";
      log.error(errorMsg, ex);
      return ResponseEntity.internalServerError().body(errorMsg);
    }
  }

  @Override
  public ResponseEntity<String> proceedSaveFile(String fileName, ByteArrayResource byteArrayResource, boolean overwrite) {
    if (!overwrite && fileHolder.exists(fileName)) {
      return ResponseEntity.badRequest().body("File with such name already stored on server. " +
          "Consider adding overwrite=true request header to overwrite existing file or change file name");
    }

    try {
      var saved = fileHolder.saveFile(fileName, byteArrayResource);
      if (saved) {
        return ResponseEntity.ok().body(String.format("File %s saved successfully on server", fileName));
      }
      return ResponseEntity.badRequest().body("File is either empty or absent, nothing to store");
    } catch (Exception ex) {
      log.error("Server error during 'save file' operation!", ex);
      return ResponseEntity.internalServerError().body(ex.getMessage());
    }
  }

  @Override
  public ResponseEntity<Integer> getFilesAmount() {
    var fileHolderResponse = fileHolder.getFilesAmount();
    if (fileHolderResponse >= 0) {
      return ResponseEntity.ok().body(fileHolderResponse);
    } else {
      return ResponseEntity.internalServerError().build();
    }
  }


}
