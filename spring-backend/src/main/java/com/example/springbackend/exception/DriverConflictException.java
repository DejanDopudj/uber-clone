package com.example.springbackend.exception;

public class DriverConflictException extends RuntimeException {
    public DriverConflictException() {
    }

    public DriverConflictException(String message) {
        super(message);
    }

    public DriverConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
