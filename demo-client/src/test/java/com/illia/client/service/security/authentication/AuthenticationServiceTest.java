package com.illia.client.service.security.authentication;


import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.ENCODED_PASSWORD;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.JWT_VALUE;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.RAW_PASSWORD;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.VALID_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.illia.client.persistence.security.entity.Role;
import com.illia.client.persistence.security.entity.User;
import com.illia.client.service.security.CustomAuthenticationException;
import com.illia.client.service.security.UserService;
import com.illia.client.service.security.jwt.JwtService;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

  private static final User preparedUsed = User.builder()
      .username(VALID_USERNAME)
      .password(ENCODED_PASSWORD)
      .roles(Set.of(Role.USER))
      .build();

  private static final Authentication preparedAuthentication = new UsernamePasswordAuthenticationToken(
      VALID_USERNAME, RAW_PASSWORD);

  @Test
  public void processRegistrationRequestShouldReturnCreatedToken() {
    when(authenticationManager.authenticate(
        eq(preparedAuthentication)))
        .thenReturn(new UsernamePasswordAuthenticationToken(VALID_USERNAME, ENCODED_PASSWORD));

    when(userService.saveUser(eq(preparedUsed)))
        .thenReturn(preparedUsed);

    when(jwtService.createToken(eq(preparedUsed)))
        .thenReturn(JWT_VALUE);

    assertEquals(JWT_VALUE,
        authenticationService.processRegistrationRequest(VALID_USERNAME, RAW_PASSWORD));

    verify(authenticationManager, times(1))
        .authenticate(eq(preparedAuthentication));
    verify(userService, times(1))
        .saveUser(eq(preparedUsed));
    verify(jwtService, times(1))
        .createToken(eq(preparedUsed));
  }

  @Test
  public void processRegistrationRequestShouldThrowAuthenticationException() {
    var exceptionMsg = "Bad credentials!";
    when(authenticationManager.authenticate(
        eq(preparedAuthentication)))
        .thenThrow(new CustomAuthenticationException(exceptionMsg));

    var ex = assertThrows(AuthenticationException.class,
        () -> authenticationService.processRegistrationRequest(VALID_USERNAME, RAW_PASSWORD));

    assertEquals(exceptionMsg, ex.getMessage());
    verify(authenticationManager, times(1))
        .authenticate(eq(preparedAuthentication));
  }

  @Test
  public void processLoginRequestShouldReturnCreatedToken() {
    when(authenticationManager.authenticate(
        eq(preparedAuthentication)))
        .thenReturn(new UsernamePasswordAuthenticationToken(VALID_USERNAME, ENCODED_PASSWORD));

    when(userService.getUserByUsername(eq(VALID_USERNAME)))
        .thenReturn(preparedUsed);

    when(jwtService.createToken(eq(preparedUsed)))
        .thenReturn(JWT_VALUE);

    assertEquals(JWT_VALUE,
        authenticationService.processLoginRequest(VALID_USERNAME, RAW_PASSWORD));

    verify(authenticationManager, times(1))
        .authenticate(eq(preparedAuthentication));
    verify(userService, times(1))
        .getUserByUsername(eq(VALID_USERNAME));
    verify(jwtService, times(1))
        .createToken(eq(preparedUsed));
  }

  @Test
  public void processLoginRequestShouldThrowAuthenticationException() {
    var exceptionMsg = "Bad credentials!";
    when(authenticationManager.authenticate(
        eq(preparedAuthentication)))
        .thenThrow(new CustomAuthenticationException(exceptionMsg));

    var ex = assertThrows(AuthenticationException.class,
        () -> authenticationService.processLoginRequest(VALID_USERNAME, RAW_PASSWORD));

    assertEquals(exceptionMsg, ex.getMessage());
    verify(authenticationManager, times(1))
        .authenticate(eq(preparedAuthentication));
  }
}
