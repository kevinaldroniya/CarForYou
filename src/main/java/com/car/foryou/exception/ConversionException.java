package com.car.foryou.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConversionException extends RuntimeException {
    private final HttpStatus status;
    public ConversionException(String source, String target, HttpStatus status) {
        super(String.format("Error while conversion %s to %s", source, target));
        this.status = status;
    }
}
