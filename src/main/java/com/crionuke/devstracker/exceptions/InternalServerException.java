package com.crionuke.devstracker.exceptions;

public class InternalServerException extends Exception {
    static public final String ID = "internal_server_error";

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
