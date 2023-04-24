package com.illia.client.model.auth;


import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
  USER, ADMIN;

  @Override
  public String getAuthority() {
    return this.toString();
  }
}
