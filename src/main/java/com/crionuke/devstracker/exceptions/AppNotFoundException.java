package com.crionuke.devstracker.exceptions;

public class AppNotFoundException extends Exception {
    static public final String ID = "app_not_found";

    public AppNotFoundException(String message) {
        super(message);
    }
}
