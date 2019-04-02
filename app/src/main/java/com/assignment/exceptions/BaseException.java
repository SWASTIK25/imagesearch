package com.assignment.exceptions;

public class BaseException extends Exception {

    private static final String MESSAGE = "Unknown Exception";

    public BaseException() {
        super(MESSAGE);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(MESSAGE + " : " + message, cause);
    }
}

