package com.illia.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "server.files")
public class ServerConfig {
    private String savedFilesDirectory;
}
