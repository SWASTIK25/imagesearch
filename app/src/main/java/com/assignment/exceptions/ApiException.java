package com.assignment.exceptions;

public class ApiException extends Exception {

    private static final String MESSAGE = "Unknown Exception";

    public ApiException() {
        super(MESSAGE);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(MESSAGE + " : " + message, cause);
    }
}

