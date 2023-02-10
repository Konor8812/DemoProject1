package com.illia.client.config;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(ClientConfig.class)
public class ApplicationConfig {

  /**
   * Deletes everything from demo-client/downloadedFiles/
   */

  @Bean
  CommandLineRunner runner(ClientConfig clientConfig) {
    return args -> {
      var directoryPath = Path.of(clientConfig.getDownloadedFilesDirectoryPrefix());
      FileUtils.deleteDirectory(directoryPath.toFile());
      Files.createDirectory(directoryPath);
    };
  }

  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
