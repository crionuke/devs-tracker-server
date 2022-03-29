package com.crionuke.devstracker.exceptions;

public class MaxTrackersLimitReachedException extends Exception {
    static public final String ID = "max_limit_reached";

    public MaxTrackersLimitReachedException(String message) {
        super(message);
    }
}
