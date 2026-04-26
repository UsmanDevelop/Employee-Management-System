package com.ems.department_service.exception;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AllExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllException(Exception e){

        Map<String, Object> error = new HashMap<>();
        error.put("timeStamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        error.put("error", "Internal Server Error");
        error.put("message", e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(Exception e){

        Map<String, Object> error = new HashMap<>();
        error.put("timeStamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND);
        error.put("error", "Not Found");
        error.put("message", e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
