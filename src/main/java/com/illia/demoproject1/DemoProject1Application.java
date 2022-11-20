package com.illia.demoproject1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DemoProject1Application {

    public static void main(String[] args) {
//        SpringApplication.run(DemoProject1Application.class, args);
        new SpringApplicationBuilder().sources(DemoProject1Application.class).run(args);
    }


}
