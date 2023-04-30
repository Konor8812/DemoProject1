package com.illia.client.constants;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestConstants {


  public static class AuthenticationTestConstants {
    public static final String VALID_USERNAME = "username";
    public static final String INVALID_USERNAME = "";
    public static final String RAW_PASSWORD = "password";
    // is it clearer to hardcode encoded value?
    public static final String ENCODED_PASSWORD = new BCryptPasswordEncoder().encode(RAW_PASSWORD);
    public static final String SIGNING_KEY = "12345678123456781234567812345678";
    public static final String JWT_HEADER = "Bearer SOME.JWT.TOKEN";
    public static final String JWT_VALUE = "SOME.JWT.TOKEN";
    public static final String PREPARED_EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImV4cCI6MTY4Mjg2NzMxNiwiaWF0IjoxNjgyODY3MzE1fQ.tbYqhHZkdrsdx9MYAkKKGcCfU4E9y4lfYWAmELYlXF0";
  }
}
