package com.illia.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "server")
public class ClientConfig {
    String port;
    String baseUrl;
}
