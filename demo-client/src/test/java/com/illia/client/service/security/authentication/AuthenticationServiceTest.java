package com.illia.client.service.security.authentication;


import com.illia.client.model.dto.AuthenticationRequestDto;
import com.illia.client.service.security.jwt.JwtService;
import com.illia.client.service.security.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(classes = {AuthenticationService.class})
public class AuthenticationServiceTest {

  @Autowired
  AuthenticationService authenticationService;

  @MockBean
  JwtService jwtService;
  @MockBean
  AuthenticationManager authenticationManager;
  @MockBean
  UserService userService;
  @MockBean
  PasswordEncoder passwordEncoder;

  AuthenticationRequestDto preparedAuthenticationRequestDto = AuthenticationRequestDto.builder()
      .username("username")
      .password("password")
      .build();

  @Test
  public void processRegistrationRequestShouldReturnSavedUser(){

    var res = authenticationService.processRegistrationRequest(preparedAuthenticationRequestDto);

  }

}
