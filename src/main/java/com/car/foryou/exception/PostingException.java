package com.car.foryou.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PostingException extends RuntimeException {
  private final HttpStatus status;

  public PostingException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

}
