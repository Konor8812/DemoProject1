package com.illia.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "client")
public class ClientConfig {

  private String baseUrl;
  private String downloadedFilesDirectoryPrefix;
}
