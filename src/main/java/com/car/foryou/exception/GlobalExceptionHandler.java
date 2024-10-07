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
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream().map(
                fieldError -> String.format("%s", fieldError.getDefaultMessage()))
                .findFirst().orElse("Field Validation Error");
        ex.getBindingResult().getFieldError();
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(status.value())
                .message(status.toString().split(" ")[1])
                .details(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest webRequest){
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(HttpStatus.NOT_FOUND.value())
                .message(HttpStatus.NOT_FOUND.getReasonPhrase())
                .details(e.getMessage())
                .path(webRequest.getDescription(false).replace("uri=",""))
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ErrorDetails> handlePostingException(GeneralException e, WebRequest webRequest){
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(e.getStatus().value())
                .message(e.getStatus().getReasonPhrase())
                .details(e.getMessage())
                .path(webRequest.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorDetails, e.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException e, WebRequest webRequest){
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().orElseThrow();
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .details(constraintViolation.getMessage())
                .path(webRequest.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e, WebRequest webRequest){
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(HttpStatus.CONFLICT.value())
                .message(HttpStatus.CONFLICT.getReasonPhrase())
                .details(e.getMessage())
                .path(webRequest.getDescription(false).replace("uri=",""))
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
