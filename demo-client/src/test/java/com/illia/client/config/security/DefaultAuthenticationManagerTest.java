package com.illia.client.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.ENCODED_PASSWORD;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.RAW_PASSWORD;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.VALID_USERNAME;

import com.illia.client.constants.TestConstants.AuthenticationTestConstants;
import com.illia.client.persistence.security.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest(classes = {DefaultAuthenticationManager.class})
public class DefaultAuthenticationManagerTest {

  @Autowired
  DefaultAuthenticationManager authenticationManager;

  @MockBean
  BCryptPasswordEncoder encoder;
  @MockBean
  UserDetailsService userDetailsService;

  @Test
  public void shouldAuthenticateValidInput() {

    var validAuthenticationToken = new UsernamePasswordAuthenticationToken(VALID_USERNAME, RAW_PASSWORD);
    when(encoder.matches(eq(RAW_PASSWORD), anyString()))
        .thenReturn(true);

    when(userDetailsService.loadUserByUsername(eq(VALID_USERNAME)))
        .thenReturn(User.builder()
            .username(VALID_USERNAME)
            .password(ENCODED_PASSWORD)
            .build());

    var resp = authenticationManager.authenticate(validAuthenticationToken);

    assertEquals(VALID_USERNAME, resp.getPrincipal());
    assertNotEquals(RAW_PASSWORD, resp.getCredentials());
    verify(userDetailsService, times(1))
        .loadUserByUsername(eq(VALID_USERNAME));
  }

  @Test
  public void shouldNotAuthenticateInvalidInput() {
    var authenticationToken = new UsernamePasswordAuthenticationToken(
        VALID_USERNAME, RAW_PASSWORD);
    when(userDetailsService.loadUserByUsername(eq(VALID_USERNAME)))
        .thenReturn(User.builder()
            .username(VALID_USERNAME)
            .password("")
            .build());

    var ex = assertThrows(AuthenticationException.class,
        () -> authenticationManager.authenticate(authenticationToken));

    assertEquals("Bad credentials", ex.getMessage());

    verify(userDetailsService, times(1))
        .loadUserByUsername(eq(VALID_USERNAME));
  }

}
