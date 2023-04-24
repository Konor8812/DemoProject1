package com.illia.client.config;

import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class JwtSecretProvider {

  private final String secret = "51655468566D597133743677397A24432646294A404E635266556A586E5A7234";
  private final byte[] encodedSecret = Base64.getEncoder().encode(secret.getBytes());

  public byte[] getEncodedSecret() {
    return encodedSecret;
  }
}
