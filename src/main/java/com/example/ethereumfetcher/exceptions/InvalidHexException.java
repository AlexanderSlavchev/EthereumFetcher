package com.example.ethereumfetcher.exceptions;

public class InvalidHexException extends RuntimeException {
    public InvalidHexException(String message) {
        super(message);
    }

    public InvalidHexException(String message, Throwable cause) {
        super(message, cause);
    }

}
