package com.illia.client.filter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.illia.client.service.security.jwt.JwtService;
import javax.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


@SpringBootTest(classes = {JwtAuthenticationFilter.class})
public class JwtAuthenticationFilterTest {

  @Autowired
  JwtAuthenticationFilter filter;
  @MockBean
  JwtService jwtService;
  @MockBean
  UserDetailsService userDetailsService;

  private static final String JWT_HEADER = "Bearer SOME.JWT.TOKEN";
  private static final String JWT_VALUE = "SOME.JWT.TOKEN";
  private static final String USERNAME = "username";

  @Test
  public void shouldHandleRequestWithJwtHeaderAndCreateAuthentication() throws Exception {
    when(jwtService.containsValidToken(eq(JWT_HEADER)))
        .thenReturn(true);
    when(jwtService.extractUsername(eq(JWT_VALUE)))
        .thenReturn(USERNAME);
    when(userDetailsService.loadUserByUsername(eq(USERNAME)))
        .thenReturn(mock(UserDetails.class));

    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", JWT_HEADER);

    var response = new MockHttpServletResponse();
    var filterChain = mock(FilterChain.class);

    filter.doFilter(request,response, filterChain);

    verify(jwtService, times(1))
        .containsValidToken(eq(JWT_HEADER));
    verify(jwtService, times(1))
        .extractUsername(eq(JWT_VALUE));
    verify(userDetailsService, times(1))
        .loadUserByUsername(eq(USERNAME));

    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
  }

  @Test
  public void shouldNotCreateAuthenticationIfNoJwtPresent() throws Exception {
    when(jwtService.containsValidToken(any()))
        .thenReturn(false);

    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", JWT_HEADER);

    var response = new MockHttpServletResponse();
    var filterChain = mock(FilterChain.class);

    filter.doFilter(request, response, filterChain);

    verify(jwtService, times(1))
        .containsValidToken(eq(JWT_HEADER));
    verify(jwtService, never())
        .extractUsername(any());
    verify(userDetailsService, never())
        .loadUserByUsername(any());

    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNull(authentication);
  }
}

