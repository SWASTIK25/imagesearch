package com.assignment.exceptions;

public class FileAlreadyExistException extends BaseException {

    private static final String MESSAGE = "File already exits.";
    public FileAlreadyExistException() {

    }

    public FileAlreadyExistException(String message) {
        super(MESSAGE + " : "+ message);
    }

    public FileAlreadyExistException(String message, Throwable cause) {
        super(MESSAGE + " : " + message, cause);
    }
}
