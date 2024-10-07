package com.car.foryou.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Getter
public class ResourceAlreadyExistsException extends RuntimeException {
    private final HttpStatus status;
    public ResourceAlreadyExistsException(String source, HttpStatus status) {
        super(String.format("%s with given attributes has already exists", source));
        this.status=status;
    }
}
