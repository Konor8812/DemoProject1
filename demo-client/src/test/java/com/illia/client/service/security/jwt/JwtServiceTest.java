package com.illia.client.service.security.jwt;

import com.illia.client.config.security.JwtSecretProvider;
import com.illia.client.persistence.security.entity.User;
import io.jsonwebtoken.Jwts;
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


  @Test
  public void shouldReturnValidToken(){
    var prepared = User.builder()
        .username("username")
        .password("password")
        .build();

    var token = jwtService.createToken(prepared);

  }

}
