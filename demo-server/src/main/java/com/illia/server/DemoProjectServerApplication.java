package com.illia.server;


import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class DemoProjectServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(DemoProjectServerApplication.class).run(args);
    }

    /**
     * Deletes everything from demo-server/savedFiles/
     */

    @Bean
    CommandLineRunner runner(){
        return args ->{
            var directoryPath = Path.of("demo-server/savedFiles/");
            FileUtils.deleteDirectory(directoryPath.toFile());
            Files.createDirectory(directoryPath);
        };
    }
}
