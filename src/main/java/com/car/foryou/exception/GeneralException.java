package com.car.foryou.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GeneralException extends RuntimeException {
  private final HttpStatus status;
  public GeneralException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

}
