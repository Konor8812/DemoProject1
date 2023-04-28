package com.illia.client.constants;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestConstants {


  public static class AuthenticationTestConstants {
    public static final String VALID_USERNAME = "username";
    public static final String INVALID_USERNAME = "";
    public static final String RAW_PASSWORD = "password";
    // is it clearer to hardcode encoded value?
    public static final String ENCODED_PASSWORD = new BCryptPasswordEncoder().encode(RAW_PASSWORD);
  }
}
