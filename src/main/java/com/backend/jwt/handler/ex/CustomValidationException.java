package com.backend.jwt.handler.ex;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;


@AllArgsConstructor
@Getter
public class CustomValidationException extends RuntimeException{

    public static final long serialVersionUID = 1L;
    private String message;
    private Map<String ,String> errorMap;

    public CustomValidationException(String message) {
        this.message = message;
    }
}
