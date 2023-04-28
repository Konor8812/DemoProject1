package com.illia.client.service.security.authentication;

import com.illia.client.persistence.security.entity.Role;
import com.illia.client.persistence.security.entity.User;
import com.illia.client.service.security.CustomAuthenticationException;
import com.illia.client.service.security.jwt.JwtService;
import com.illia.client.service.security.UserService;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

  JwtService jwtService;
  AuthenticationManager authenticationManager;
  UserService userService;

  public String processRegistrationRequest(String username, String password) {
    var authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));
    var savedUser = userService.saveUser(User.builder()
        .username(username)
        .password((String)authentication.getCredentials())
        .roles(Set.of(Role.USER))
        .build());
    return jwtService.createToken(savedUser);
  }

  public String processLoginRequest(String username, String password) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));

    return jwtService.createToken(userService.getUserByUsername(username));
  }
}
