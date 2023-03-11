package com.illia.server.config;


import com.mongodb.ConnectionString;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

@Configuration
public class DatasourceConfig {

  @Bean
  public MongoClientFactoryBean mongo() throws Exception {
    String connectionString;

    if((connectionString = System.getenv("mongodb-connection-uri")) == null){
      if ((connectionString = readConnectionString()) == null){
        throw new Exception();
      }
    }

    var mongoClientFactory = new MongoClientFactoryBean();
    mongoClientFactory.setConnectionString(
        new ConnectionString(
            connectionString));
    return mongoClientFactory;
  }

  private String readConnectionString() throws IOException {
    return Files.readString(Path.of("mongo-client-uri.txt"));
  }


}