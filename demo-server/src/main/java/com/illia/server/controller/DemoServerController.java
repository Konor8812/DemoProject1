package com.illia.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illia.server.file.model.FileEntity.FileDocument;
import com.illia.server.request.RequestProcessor;
import com.illia.server.request.RequestProcessorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("demo")
public class DemoServerController {

  @Autowired
  private RequestProcessor requestProcessor;
  @Autowired
  ObjectMapper objectMapper;

  @PostMapping(value = "/uploadFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<String> uploadFile(@RequestParam(name = "fileName") String fileName,
      @RequestHeader(name = "Overwrite", required = false, defaultValue = "false") Boolean overwrite,
      @RequestPart(required = false, name = "resource") ByteArrayResource byteArrayResource)
      throws RequestProcessorException {
    return ResponseEntity.ok(
        requestProcessor.proceedSaveFile(fileName, byteArrayResource, overwrite));
  }


  @GetMapping(value = "/downloadFile", consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<FileDocument> downloadFile(@RequestParam(name = "fileName") String fileName)
      throws RequestProcessorException {
    return ResponseEntity.ok().body(requestProcessor.proceedDownloadFile(fileName));
  }


  @GetMapping(value = "/count", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getAmount() throws JsonProcessingException {
    return ResponseEntity.ok(objectMapper.writeValueAsString(requestProcessor.getFilesAmount()));
  }

  @GetMapping(value = "/all", consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> getAll() throws JsonProcessingException {
    return ResponseEntity
        .ok(objectMapper.writeValueAsString(requestProcessor.getAllSavedFiles()));
  }


  @ExceptionHandler(value = {RequestProcessorException.class})
  public ResponseEntity<String> handleRequestProcessorException(Exception ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }


}
