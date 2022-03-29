package com.crionuke.devstracker.exceptions;

public class FreeTrackersLimitReachedException extends Exception {
    static public final String ID = "free_limit_reached";

    public FreeTrackersLimitReachedException(String message) {
        super(message);
    }
}
