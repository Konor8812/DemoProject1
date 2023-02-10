package com.illia.server.config;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ServerConfig.class)
public class ApplicationConfig {


  /**
   * Deletes everything from demo-server/savedFiles/
   */

  @Bean
  CommandLineRunner runner() {
    return args -> {
      var directoryPath = Path.of("demo-server/savedFiles/");
      FileUtils.deleteDirectory(directoryPath.toFile());
      Files.createDirectory(directoryPath);
    };
  }
}
