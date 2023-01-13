package com.illia.client.service.processor;

public class InvalidAttributeException extends RuntimeException{
    public InvalidAttributeException(String msg){
        super(msg);
    }
}
