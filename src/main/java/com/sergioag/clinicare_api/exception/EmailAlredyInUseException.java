package com.sergioag.clinicare_api.exception;

public class EmailAlredyInUseException extends RuntimeException {
    public EmailAlredyInUseException(String message) {
        super(message);
    }
}
