package com.illia.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DemoProjectClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(DemoProjectClientApplication.class).run(args);
    }
}
