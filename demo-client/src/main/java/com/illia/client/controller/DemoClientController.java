package com.illia.client.controller;

import com.illia.client.model.request.entity.QueryEntity;
import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.file.FileHandlingService;
import com.illia.client.service.file.FileTransferService;
import com.illia.client.service.query.QueryProcessingException;
import com.illia.client.service.query.QueryProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("demo")
public class DemoClientController {

  @Autowired
  private FileTransferService fileTransferService;

  @Autowired
  private FileHandlingService fileHandlingService;

  @Autowired
  private QueryProcessingService queryProcessingService;

  @PostMapping(value = "/uploadFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<String> uploadFile(@RequestParam(name = "fileName") String fileName,
      @RequestHeader(name = "Overwrite", required = false, defaultValue = "false") Boolean overwrite,
      @RequestPart MultipartFile multipartFile) throws FileHandlingException {
    return fileTransferService.uploadFile(fileName, multipartFile, overwrite);
  }

  @GetMapping("/downloadFile")
  public ResponseEntity<String> downloadFile(@RequestParam(name = "fileName") String fileName,
      @RequestParam(name = "overwrite", required = false, defaultValue = "false") Boolean overwrite)
      throws FileHandlingException {
    return ResponseEntity.ok().body(fileTransferService.downloadFile(fileName, overwrite));
  }

  @GetMapping("/deleteFile")
  public ResponseEntity<String> deleteFile(@RequestParam(name = "fileName") String fileName)
      throws FileHandlingException {
    return ResponseEntity.ok().body(fileHandlingService.deleteFile(fileName));
  }

  @PostMapping(value = "/query", consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Object> performOperation(@RequestBody QueryEntity queryEntity)
      throws QueryProcessingException {
    return ResponseEntity.ok().body(queryProcessingService.performOperation(queryEntity));
  }
}
