package com.car.foryou.exception;

import com.car.foryou.dto.ErrorDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> String.format("%s", fieldError.getField())).findFirst().orElse("Field Validation Error");
        ex.getBindingResult().getFieldError();


        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .status("400")
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Field Validation Error")
                .details(String.format("Invalid value for property '%s'", message))
                .path(request.getDescription(false))
                .exception(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest webRequest){
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .status("404")
                .error(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                .message("Resource not found")
                .details(e.getMessage())
                .path(webRequest.getDescription(false))
                .exception(e.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
