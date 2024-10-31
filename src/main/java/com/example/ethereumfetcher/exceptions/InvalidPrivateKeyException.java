package com.example.ethereumfetcher.exceptions;

public class InvalidPrivateKeyException extends RuntimeException {
    public InvalidPrivateKeyException(String message) {
        super(message);
    }

    public InvalidPrivateKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
