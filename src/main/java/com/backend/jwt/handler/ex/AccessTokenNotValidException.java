package com.backend.jwt.handler.ex;

public class AccessTokenNotValidException extends RuntimeException{
    public AccessTokenNotValidException(String message) {
        super(message);
    }
}
