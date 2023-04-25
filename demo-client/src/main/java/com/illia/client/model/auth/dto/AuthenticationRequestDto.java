package com.illia.client.model.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRequestDto {

  private String username;
  private String password;
}