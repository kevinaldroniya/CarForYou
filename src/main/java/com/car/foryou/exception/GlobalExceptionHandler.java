package com.car.foryou.exception;

import com.car.foryou.dto.ErrorDetails;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> String.format("%s", fieldError.getDefaultMessage())).findFirst().orElse("Field Validation Error");
        ex.getBindingResult().getFieldError();


        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Method Argument Not Valid")
                .details(message)
                .path(request.getDescription(false))
                .exception(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest webRequest){
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                .message("Resource not found")
                .details(e.getMessage())
                .path(webRequest.getDescription(false))
                .exception(e.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PostingException.class)
    public ResponseEntity<ErrorDetails> handlePostingException(PostingException e, WebRequest webRequest){
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(e.getStatus().getReasonPhrase())
                .message("Posting Error")
                .details(e.getMessage())
                .path(webRequest.getDescription(false).replace("uri=", ""))
                .exception(e.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, e.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException e, WebRequest webRequest){
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().orElseThrow();
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Constraint Violation Error")
                .details(constraintViolation.getMessage())
                .path(webRequest.getDescription(false).replace("uri=", ""))
                .exception(e.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
