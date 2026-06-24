package com.badminton.platform.exception;

public class EmailExistsException extends RuntimeException {

    public EmailExistsException() {
        super("EMAIL_EXISTS");
    }
}
