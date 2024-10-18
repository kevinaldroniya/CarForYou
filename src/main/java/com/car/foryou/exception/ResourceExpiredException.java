package com.car.foryou.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class ResourceExpiredException extends RuntimeException{
    public ResourceExpiredException(String source) {
        super(String.format("%s has expired", source));
    }
}
