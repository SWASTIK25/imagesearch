package com.assignment.exceptions;

public class NoDataException extends BaseException {

    private static final String MESSAGE = "Required data not available";
    public NoDataException() {

    }

    public NoDataException(String message) {
        super(MESSAGE + " : "+ message);
    }

    public NoDataException(String message, Throwable cause) {
        super(MESSAGE + " : " + message, cause);
    }
}
