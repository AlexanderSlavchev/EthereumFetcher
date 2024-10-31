package com.example.ethereumfetcher.advice;

import com.example.ethereumfetcher.exceptions.AuthenticationException;
import com.example.ethereumfetcher.exceptions.InvalidHexException;
import com.example.ethereumfetcher.exceptions.InvalidTokenException;
import com.example.ethereumfetcher.exceptions.InvalidTransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "An unexpected error occurred: " + exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(InvalidTokenException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Invalid token: " + exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);  // HTTP 401 Unauthorized
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTransactionException(InvalidTransactionException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Invalid transaction: " + exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);  // HTTP 400 Bad Request
    }

    @ExceptionHandler(InvalidHexException.class)
    public ResponseEntity<Map<String, String>> handleInvalidHexException(InvalidHexException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Invalid hex: " + exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);  // HTTP 400 Bad Request
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,String>> handleAccessDeniedException(AccessDeniedException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Invalid transaction: " + exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

}