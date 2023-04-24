package com.illia.client.service.security.authentication;

import com.illia.client.model.auth.Role;
import com.illia.client.model.auth.User;
import com.illia.client.model.auth.dto.AuthenticationRequestDto;
import com.illia.client.service.security.AuthenticationException;
import com.illia.client.service.security.JwtService;
import com.illia.client.service.security.UserService;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

  JwtService jwtService;
  AuthenticationManager authenticationManager;
  UserService userService;
  PasswordEncoder passwordEncoder;

  public String processRegistrationRequest(AuthenticationRequestDto authenticationRequestDto) {
    var savedUser = userService.saveUser(User.builder()
        .username(authenticationRequestDto.getUsername())
        .password(authenticationRequestDto.getPassword())
        .roles(Set.of(Role.USER))
        .build());
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(savedUser.getUsername(), savedUser.getPassword()));


    return jwtService.createToken(savedUser);
  }

  public String processLoginRequest(AuthenticationRequestDto authenticationRequestDto) {
    var user = userService.getUserByUsername(authenticationRequestDto.getUsername());

    if (passwordEncoder.matches(authenticationRequestDto.getPassword(), user.getPassword())) {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

      return jwtService.createToken(user);
    } else {
      throw new AuthenticationException("Bad credentials");
    }
  }
}
