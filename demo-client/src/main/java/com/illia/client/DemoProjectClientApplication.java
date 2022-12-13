package com.illia.client;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class DemoProjectClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(DemoProjectClientApplication.class).run(args);
    }

    /**
     * Deletes everything from demo-server/savedFiles/
     */

    @Bean
    CommandLineRunner runner(){
        return args ->{
            var directoryPath = Path.of("demo-client/savedFiles/");
            FileUtils.deleteDirectory(directoryPath.toFile());
            Files.createDirectory(directoryPath);
        };
    }

}
