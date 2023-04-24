package com.illia.client.controller;

import com.illia.client.model.auth.dto.AuthenticationRequestDto;
import com.illia.client.service.security.AuthenticationException;
import com.illia.client.service.security.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class AuthenticationController {

  @Autowired
  AuthenticationService authenticationService;

  @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> registrationRequest(
      @RequestBody AuthenticationRequestDto authenticationRequestDto) {
    return ResponseEntity.ok(
        authenticationService.processRegistrationRequest(
           authenticationRequestDto));
  }

  @PostMapping("/login")
  public ResponseEntity<String> loginRequest(
      @RequestBody AuthenticationRequestDto authenticationRequestDto) {
    return ResponseEntity.ok(
        authenticationService.processLoginRequest(authenticationRequestDto));
  }

}
