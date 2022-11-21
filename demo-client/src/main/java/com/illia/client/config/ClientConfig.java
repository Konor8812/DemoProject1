package com.illia.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "client")
public class ClientConfig {
    String baseUrl;

    public String getServerBaseUrl() {
        return baseUrl;
    }
}
