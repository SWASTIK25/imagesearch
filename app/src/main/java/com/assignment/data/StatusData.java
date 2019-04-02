package com.assignment.data;


import com.assignment.common.interfaces.IAction;

public class StatusData {

    public enum Status {
        SUCCESS, FAIL
    }

    private Status status;
    private IAction action;
    private Throwable throwable;
    private String message;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public IAction getAction() {
        return action;
    }

    public void setAction(IAction action) {
        this.action = action;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
