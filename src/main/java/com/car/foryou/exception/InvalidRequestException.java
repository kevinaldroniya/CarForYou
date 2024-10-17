package com.car.foryou.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {
    private final HttpStatus status;
    public InvalidRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
