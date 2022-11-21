package com.illia.client.http_client;


import com.illia.client.config.ClientConfig;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

@Component
public class MyHttpClientImpl implements MyHttpClient {

    @Autowired
    ClientConfig clientConfig;

    @Override
    public File uploadFile(String fileName, File file) {
        var operation = "/uploadFile";
        var params = String.format("?fileName=%s", fileName);
        var url = String.format("%s%s%s", clientConfig.getServerBaseUrl(), operation, params);

        System.out.println(url);

        try {
            var httpClient = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(Path.of("downloaded.txt")));
            var body = response.body();


            return new File(body.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public File downloadFile(String fileName) {
        return null;
    }


}
