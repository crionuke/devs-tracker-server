package com.crionuke.devstracker.exceptions;

public class TrackerAlreadyAddedException extends Exception {
    static public final String ID = "already_added";

    public TrackerAlreadyAddedException(String message, Throwable cause) {
        super(message, cause);
    }
}
