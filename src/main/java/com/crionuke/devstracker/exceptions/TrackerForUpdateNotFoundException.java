package com.crionuke.devstracker.exceptions;

public class TrackerForUpdateNotFoundException extends Exception {
    static public final String ID = "tracker_not_found";

    public TrackerForUpdateNotFoundException(String message) {
        super(message);
    }
}
