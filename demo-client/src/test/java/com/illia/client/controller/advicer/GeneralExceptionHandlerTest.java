package com.illia.client.controller.advicer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.illia.client.controller.AuthenticationController;
import com.illia.client.controller.DemoClientController;
import com.illia.client.controller.advice.GeneralExceptionHandler;
import com.illia.client.service.file.FileHandlingException;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {GeneralExceptionHandler.class,
    DemoClientController.class,
    AuthenticationController.class})
@AutoConfigureMockMvc
public class GeneralExceptionHandlerTest {
  @Autowired
  MockMvc mvc;


  @Test
  public void testGeneralExceptionHandler(){


  }

}
