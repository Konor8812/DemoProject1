package com.illia.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DemoProjectServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(DemoProjectServerApplication.class).run(args);
    }

}
