package com.illia.client.http_client;

public class HttpClientException extends RuntimeException{
    public HttpClientException(String msg, Throwable th){
        super(msg, th);
    }
    public HttpClientException(String msg){
        super(msg);
    }
    public HttpClientException(){

    }
}
