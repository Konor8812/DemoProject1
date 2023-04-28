package com.illia.client.filter;

import com.illia.client.service.security.CustomAuthenticationException;
import com.illia.client.service.security.jwt.JwtService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    var jwtHeader = request.getHeader("Authorization");
    if (jwtService.containsValidToken(jwtHeader)) {
      var token = jwtHeader.substring(7);
        var username = jwtService.extractUsername(token);
        var user = userDetailsService.loadUserByUsername(username);
        var authentication = createAuthentication(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  private Authentication createAuthentication(UserDetails user) {
    if (user != null) {
      return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    } else {
      throw new CustomAuthenticationException("No such user / invalid jwt");
    }
  }

}
