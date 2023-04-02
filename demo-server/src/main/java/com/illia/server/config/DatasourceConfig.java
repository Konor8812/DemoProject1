package com.illia.server.config;


import com.mongodb.ConnectionString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

@Configuration
public class DatasourceConfig {

  @Bean
  public MongoClientFactoryBean mongoClientFactoryBean(@Value(value = "${spring.data.mongodb.uri:}") String connectionString) throws Exception {
    if (!isConnectionStringValid(connectionString)) {
      connectionString = getConnectionStringFromEnvironment();
      if(!isConnectionStringValid(connectionString)){
        throw new Exception("No MongoDB connection string in both application.properties and environment!");
      }
    }

    var mongoClientFactory = new MongoClientFactoryBean();
    mongoClientFactory.setConnectionString(
        new ConnectionString(connectionString));
    return mongoClientFactory;
  }

  private boolean isConnectionStringValid(String connectionString){
    return connectionString == null || connectionString.isBlank();
  }

  private String getConnectionStringFromEnvironment(){
    return System.getenv("mongodb-connection-uri");
  }
}
