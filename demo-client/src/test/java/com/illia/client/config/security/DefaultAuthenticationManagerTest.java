package com.illia.client.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.illia.client.persistence.security.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

  // It is reasonable to create TestConstants
  private static final String ENCODED_PASSWORD = "$2a$10$iIlKBbk.6Dsv717xaaEuv.NPVRESIPR/BCQLGAhvbHzONTfOyg9cu"; // password

  @Test
  public void shouldAuthenticateValidInput() {
    var username = "username";
    var password = "password";
    var validAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    when(encoder.matches(eq(password), anyString()))
        .thenReturn(true);

    when(userDetailsService.loadUserByUsername(eq(username)))
        .thenReturn(User.builder()
            .username(username)
            .password(ENCODED_PASSWORD)
            .build());

    var resp = authenticationManager.authenticate(validAuthenticationToken);

    assertEquals(username, resp.getPrincipal());
    assertNotEquals(password, resp.getCredentials());
    verify(userDetailsService, times(1))
        .loadUserByUsername(eq(username));
  }

  @Test
  public void shouldNotAuthenticateInvalidInput() {
    var username = "username";
    var password = "password";
    var validAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    when(userDetailsService.loadUserByUsername(eq(username)))
        .thenReturn(User.builder()
            .username(username)
            .password("") // invalid password
            .build());

    var resp = authenticationManager.authenticate(validAuthenticationToken);

    assertNull(resp);

    verify(userDetailsService, times(1))
        .loadUserByUsername(eq(username));
  }

}
