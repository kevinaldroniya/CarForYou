package com.car.foryou.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConversionException extends RuntimeException {
    public ConversionException(String source, String target) {
        super(String.format("Error while conversion %s to %s", source, target));
    }
}
