package com.illia.client.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.illia.client.filter.JwtAuthenticationFilter;
import com.illia.client.service.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {WebSecurityConfig.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc
public class WebSecurityConfigTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  JwtService jwtService;
  @MockBean
  UserDetailsService userDetailsService;

  @Test
  public void protectedEndpointCallShouldReturn403Response() throws Exception {
    mockMvc.perform(get("/demo/uploadFile"))
        .andExpect(status().is(403));
  }

  @Test
  public void permittedEndpointShouldReturn200Response() throws Exception {
    mockMvc.perform(get("/login")) // isn't protected
        .andExpect(status().is(404)); // means request was send to servlet dispatcher
  }
}
