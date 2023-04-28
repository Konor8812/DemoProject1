package com.illia.client.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.ENCODED_PASSWORD;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.RAW_PASSWORD;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.VALID_USERNAME;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illia.client.model.dto.AuthenticationRequestDto;
import com.illia.client.service.security.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// idk why controller isn't picked by @WebMvcTest but it isn't
@Import({AuthenticationController.class})
@WebMvcTest(controllers = {AuthenticationController.class}, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  AuthenticationService authenticationService;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  public void registrationRequestTestShouldCallService() throws Exception {
    var authenticationRequestDto = AuthenticationRequestDto.builder()
        .username(VALID_USERNAME)
        .password(RAW_PASSWORD)
        .build();

    var requestBody = objectMapper.writeValueAsString(authenticationRequestDto);

    mvc.perform(post("/demo/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());

    verify(authenticationService, times(1))
        .processRegistrationRequest(eq(VALID_USERNAME), eq(RAW_PASSWORD));

  }

  @Test
  public void registrationRequestWithInvalidBodyShouldBeBadRequest() throws Exception {
    mvc.perform(post("/demo/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
        .andExpect(status().isBadRequest());
    verify(authenticationService, never())
        .processRegistrationRequest(any(), any());
  }


  @Test
  public void loginRequestTestShouldCallService() throws Exception {
    var authenticationRequestDto = AuthenticationRequestDto.builder()
        .username(VALID_USERNAME)
        .password(RAW_PASSWORD)
        .build();

    var requestBody = objectMapper.writeValueAsString(authenticationRequestDto);

    mvc.perform(post("/demo/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());

    verify(authenticationService, times(1))
        .processLoginRequest(eq(VALID_USERNAME), eq(RAW_PASSWORD));
  }

  @Test
  public void loginRequestWithInvalidBodyShouldBeBadRequest() throws Exception {
    mvc.perform(post("/demo/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
        .andExpect(status().isBadRequest());
    verify(authenticationService, never())
        .processLoginRequest(any(), any());
  }
}
