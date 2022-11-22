package com.illia.client.http_client;


import java.io.File;

public interface MyHttpClient {
    String uploadFile(String fileName, File file);
    File downloadFile(String fileName);
}
