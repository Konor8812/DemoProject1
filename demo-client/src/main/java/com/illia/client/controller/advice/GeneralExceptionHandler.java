package com.illia.client.controller.advice;

import com.illia.client.service.file.FileHandlingError;
import com.illia.client.service.file.FileHandlingException;
import com.illia.client.service.query.QueryProcessingException;
import com.illia.client.service.security.CustomAuthenticationException;
import io.jsonwebtoken.JwtException;
import java.net.ConnectException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GeneralExceptionHandler {

  @ExceptionHandler(value = {QueryProcessingException.class})
  public ResponseEntity<String> handleQueryProcessingException(Exception ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(value = {FileHandlingException.class})
  public ResponseEntity<String> handleFileHandlingException(Exception ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(value = {FileHandlingError.class})
  public ResponseEntity<String> handleFileHandlingError(Error err) {
    return ResponseEntity.internalServerError().body(err.getMessage());
  }

  @ExceptionHandler(value = {HttpClientErrorException.class})
  public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException ex) {
    if (ex.getStatusCode().is4xxClientError()) {
      return ResponseEntity.badRequest().body(ex.getResponseBodyAsString());
    } else {
      return ResponseEntity.internalServerError().body(ex.getResponseBodyAsString());
    }
  }

  @ExceptionHandler(value = {ConnectException.class})
  public ResponseEntity<String> handleConnectException(ConnectException ex) {
    return ResponseEntity.internalServerError().body("Can't connect to server");
  }

  @ExceptionHandler(value = {CustomAuthenticationException.class, JwtException.class})
  public ResponseEntity<String> handleAuthenticationException(JwtException ex) {
    return ResponseEntity.status(403).body(ex.getMessage());
  }
}
