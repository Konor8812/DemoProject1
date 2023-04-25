package com.illia.client.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationManager implements AuthenticationManager {

  @Autowired
  BCryptPasswordEncoder passwordEncoder;
  @Autowired
  UserDetailsService userDetailsService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    var username = (String) authentication.getName();

    var user = userDetailsService.loadUserByUsername(username);
    if (user != null) {
      var password = (String) authentication.getCredentials();
      var userPassword = user.getPassword();
      if (passwordEncoder.matches(password, userPassword)) {
        return new UsernamePasswordAuthenticationToken(username, userPassword);
      }
    }

    return null;
  }
}
