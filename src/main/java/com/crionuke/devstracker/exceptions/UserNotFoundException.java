package com.crionuke.devstracker.exceptions;

public class UserNotFoundException extends Exception {
    static public final String ID = "user_not_found";

    public UserNotFoundException(String message) {
        super(message);
    }
}
