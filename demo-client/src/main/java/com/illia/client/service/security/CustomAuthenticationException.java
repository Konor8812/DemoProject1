package com.illia.client.service.security;


import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {

  public CustomAuthenticationException(String msg) {
    super(msg);
  }
}
