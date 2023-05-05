package com.illia.client.controller;

import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.INVALID_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.ENCODED_PASSWORD;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.RAW_PASSWORD;
import static com.illia.client.constants.TestConstants.AuthenticationTestConstants.VALID_USERNAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illia.client.controller.advice.GeneralExceptionHandler;
import com.illia.client.model.dto.AuthenticationRequestDto;
import com.illia.client.service.security.CustomAuthenticationException;
import com.illia.client.service.security.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;

// idk why controller isn't picked by @WebMvcTest but it isn't
@Import({AuthenticationController.class, GeneralExceptionHandler.class})
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
  public void registrationRequestWithWrongCredentialsShouldBeBadRequest() throws Exception {
    var exceptionMsg = "wrong credentials";
    when(authenticationService.processRegistrationRequest(eq(INVALID_USERNAME), eq(RAW_PASSWORD)))
        .thenThrow(new CustomAuthenticationException(exceptionMsg));

    var authenticationRequestDto = AuthenticationRequestDto.builder()
        .username(INVALID_USERNAME)
        .password(RAW_PASSWORD)
        .build();
    var requestBody = objectMapper.writeValueAsString(authenticationRequestDto);

    mvc.perform(post("/demo/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().is(403))
        .andExpect(content().string(exceptionMsg));
    verify(authenticationService, times(1))
        .processRegistrationRequest(eq(INVALID_USERNAME), eq(RAW_PASSWORD));
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

  @Test
  public void loginRequestWithWrongCredentialsShouldBeBadRequest() throws Exception {
    var exceptionMsg = "wrong credentials";
    when(authenticationService.processLoginRequest(eq(INVALID_USERNAME), eq(RAW_PASSWORD)))
        .thenThrow(new CustomAuthenticationException(exceptionMsg));

    var authenticationRequestDto = AuthenticationRequestDto.builder()
        .username(INVALID_USERNAME)
        .password(RAW_PASSWORD)
        .build();
    var requestBody = objectMapper.writeValueAsString(authenticationRequestDto);

    mvc.perform(post("/demo/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().is(403))
        .andExpect(result -> {
          var exception = result.getResolvedException();
          assertTrue(exception instanceof  AuthenticationException);
          assertEquals(exceptionMsg, exception.getMessage());
        });
    verify(authenticationService, times(1))
        .processLoginRequest(eq(INVALID_USERNAME), eq(RAW_PASSWORD));
  }
}
