package com.assignment.exceptions;


public class NoMoreDataException extends BaseException {

    private static final String MESSAGE = "No More data not available";
    public NoMoreDataException() {

    }

    public NoMoreDataException(String message) {
        super(MESSAGE + " : "+ message);
    }

    public NoMoreDataException(String message, Throwable cause) {
        super(MESSAGE + " : " + message, cause);
    }
}
