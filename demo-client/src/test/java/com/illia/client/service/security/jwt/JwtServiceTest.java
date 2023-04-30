package com.illia.client.service.security.jwt;

import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.PREPARED_EXPIRED_TOKEN;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.RAW_PASSWORD;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.SIGNING_KEY;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.VALID_USERNAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.illia.client.config.security.JwtSecretProvider;
import com.illia.client.persistence.security.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = JwtService.class)
public class JwtServiceTest {

  @Autowired
  JwtService jwtService;
  @MockBean
  JwtSecretProvider secretProvider;
  private final User preparedUser = User.builder()
      .username(VALID_USERNAME)
      .password(RAW_PASSWORD)
      .build();


  @BeforeEach
  public void prepareMocks() {
    when(secretProvider.getEncodedSecret())
        .thenReturn(SIGNING_KEY.getBytes());
  }

  @Test
  public void createTokenShouldReturnToken() {
    var token = jwtService.createToken(preparedUser);
    assertTrue(token.matches("^[\\w\\-]+\\.[\\w\\-]+\\.[\\w\\-]+$"));
  }

  @Test
  public void containsValidTokenShouldReturnTrue() {
    var tokenHeader = "Bearer " + jwtService.createToken(preparedUser);
    assertTrue(jwtService.containsValidToken(tokenHeader));
  }

  @Test
  public void containsValidTokenShouldReturnFalseIfHeaderIsInvalid() {
    var tokenHeader = "Invalid header prefix " + jwtService.createToken(preparedUser);
    assertFalse(jwtService.containsValidToken(tokenHeader));
  }

  @Test
  public void containsValidTokenShouldReturnFalseIfTokenIsExpired() {
    var tokenHeader = "Bearer " + PREPARED_EXPIRED_TOKEN;
    assertFalse(jwtService.containsValidToken(tokenHeader));
  }

}
