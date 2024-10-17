package com.car.foryou.exception;

import com.car.foryou.dto.error.ErrorDetails;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.Date;
import java.util.List;

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

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorDetails> handleInvalidRequestException(InvalidRequestException e, WebRequest webRequest){
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .details(e.getMessage())
                .path(webRequest.getDescription(false).replace("uri=",""))
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<ErrorDetails> handleMessageNotReadableException(HttpMessageNotReadableException e, WebRequest webRequest){
//        ErrorDetails errorDetails = ErrorDetails.builder()
//                .timestamp(new Date())
//                .error(HttpStatus.BAD_REQUEST.value())
//                .message(HttpStatus.BAD_GATEWAY.getReasonPhrase())
//                .details(e.getMessage())
//                .path(webRequest.getDescription(false).replace("uri=",""))
//                .build();
//        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
//    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String problematicField = null;

        // Check if the cause is a JsonMappingException (for JSON parsing errors)
        if (ex.getCause() instanceof JsonMappingException jsonEx) {
            // Get the path reference (this gives you the field that caused the issue)
            List<JsonMappingException.Reference> path = jsonEx.getPath();
            if (!path.isEmpty()) {
                problematicField = path.get(0).getFieldName(); // Get the first field causing the error
            }
        }
        String formatted = String.format("Invalid value for field '%s'", problematicField);
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .error(status.value())
                .message(status.toString().split(" ")[1])
                .details(formatted)
                .path(request.getDescription(false).replace("uri=",""))
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
