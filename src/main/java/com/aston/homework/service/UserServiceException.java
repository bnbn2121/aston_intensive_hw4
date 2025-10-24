package com.aston.homework.service;

public class UserServiceException extends Exception {
    public UserServiceException() {
    }

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
