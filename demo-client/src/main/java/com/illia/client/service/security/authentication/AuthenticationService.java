package com.illia.client.service.security.authentication;

import com.illia.client.persistence.security.entity.Role;
import com.illia.client.persistence.security.entity.User;
import com.illia.client.model.dto.AuthenticationRequestDto;
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

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

    return jwtService.createToken(user);
  }
}
